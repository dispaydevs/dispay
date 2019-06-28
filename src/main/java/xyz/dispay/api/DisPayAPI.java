/*
 * DisPay: Discord Currency API
 * Copyright (C) 2019  Brett Bender & Avery Clifton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.dispay.api;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import net.dv8tion.jda.api.entities.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import xyz.dispay.DisPay;
import xyz.dispay.common.Constants;
import xyz.dispay.common.Utils;
import xyz.dispay.common.entities.Account;
import xyz.dispay.common.entities.Client;
import xyz.dispay.common.entities.Transaction;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;

import static spark.Spark.*;

public class DisPayAPI {

	private static final Logger LOG = LoggerFactory.getLogger(DisPayAPI.class);

	private final DisPay disPay;
	private SecretKey signingKey;
	private int port = Constants.DEFAULT_PORT;

	public DisPayAPI(DisPay disPay) {
		this.disPay = disPay;
	}

	/* Public Methods */

	public void generateSigningKey() {
		this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		disPay.getRedisManager().set("key", Encoders.BASE64.encode(signingKey.getEncoded()));
	}

	public void generateSystemToken() {
		String jwt = Jwts.builder()
				.setIssuedAt(Date.from(Instant.now()))
				.setId("system")
				.signWith(signingKey)
				.compact();
		disPay.getRedisManager().set("system", jwt);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() {

		String key = disPay.getRedisManager().get("key");
		if (key != null) {
			try {
				byte[] decodedKey = Base64.getDecoder().decode(key);
				this.signingKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
			} catch (Exception e) {
				LOG.warn("The signing key failed to load", e);
			}
		} else {
			generateSigningKey();
			generateSystemToken();
		}

		port(port);

		notFound((request, response) -> new JSONObject().put("message", "Resource not found"));
		internalServerError((request, response) -> new JSONObject().put("message", "Internal server error"));

		before((request, response) -> {
			LOG.info("{} {} from {} ({})", request.requestMethod(), request.uri(), request.ip(), request.userAgent());
			response.header("Content-Encoding", "gzip");
			response.type("application/json");
		});

		path("/api", () -> {
			get("", this::landing);
			path("/clients", () -> {
				post("", this::createClient);
				get("/:id", this::getClient);
			});
			post("/purchase", this::purchase);
		});

	}

	/* Utility Methods */

	private void badRequest() {
		block(400, "Bad request");
	}

	private void block(int status, String message) {
		halt(status, new JSONObject().put("message", message).toString());
	}

	private String checkAuthorization(Request request) {
		String token = request.headers("Authorization");
		if (token == null || token.isEmpty()) {
			unauthorized();
		}
		String id = "";
		// Verify that the token is untouched
		try {
			id = Jwts.parser()
					.setSigningKey(signingKey)
					.parseClaimsJws(token)
					.getBody()
					.getId();
		} catch (Exception e) {
			unauthorized();
		}
		if (id.equals("system")) {
			String verify = disPay.getRedisManager().get(id);
			if (verify == null || !verify.equals(token)) {
				unauthorized();
			}
		} else {
			Client client = disPay.getClientManager().getClient(id);
			if (client == null || !token.equals(client.getToken())) {
				unauthorized();
			}
		}
		return id;
	}

	private JSONObject checkBody(Request request) {
		// Verify the body isn't missing
		String body = request.body();
		if (body == null || body.isEmpty()) {
			block(400, "Missing body");
		}

		// Verify the body is a valid JSON object
		JSONObject data = new JSONObject();
		try {
			data = new JSONObject(body);
		} catch (Exception e) {
			block(400, "Invalid body");
		}

		return data;
	}

	private void checkSystem(Request request) {
		String id = checkAuthorization(request);
		if (!id.equals("system")) {
			unauthorized();
		}
	}

	private void unauthorized() {
		block(401, "Unauthorized");
	}

	/* Endpoints */

	private Object landing(Request request, Response response) {
		return new JSONObject()
				.put("message", "Welcome to DisPay");
	}

	private Object createClient(Request request, Response response) {
		checkSystem(request);
		JSONObject body = checkBody(request);
		if (!body.has("id") && !(body.get("id") instanceof Long)) {
			block(400, "Missing id");
		}
		if (!body.has("owner") && !(body.get("owner") instanceof Long)) {
			block(400, "Missing owner");
		}
		// Generate a token for the client
		String token = Jwts.builder()
				.setIssuedAt(Date.from(Instant.now()))
				.setId(String.valueOf(body.getLong("id")))
				.signWith(signingKey)
				.compact();
		// Create the client
		Client client = disPay.getClientManager()
				.createClient(body.getLong("id"), body.getLong("owner"), token);
		client.save();
		return new JSONObject()
				.put("token", client.getToken());
	}

	private Object getClient(Request request, Response response) {
		block(501, "Not implemented");
		return null;
	}

	private Object purchase(Request request, Response response) {
		String id = checkAuthorization(request);
		if (id.equals("system")) {
			badRequest();
		}

		// Retrieve the body
		JSONObject body = checkBody(request);

		// Verify the request contains the required fields and they are the correct type
		if (!body.has("account") || !(body.get("account") instanceof Long)) {
			block(400, "Missing account");
		}
		if (!body.has("amount") || !(body.get("amount") instanceof Integer)) {
			block(400, "Missing amount");
		}
		if (!body.has("description") || !(body.get("description") instanceof String)) {
			block(400, "Missing description");
		}

		// Verify the account is an existing discord user
		User user = Utils.getUserById(body.getLong("account"));
		if (user == null || user.isBot()) {
			block(400, "Invalid account");
		}
		Account account = disPay.getAccountManager().getAccount(user.getIdLong());

		// Verify the amount doesn't exceed the balance of the account
		long amount = body.getLong("amount");
		if (amount > account.getBalance()) {
			block(400, "Exceeds balance");
		}

		// Verify the description isn't empty
		String description = body.getString("description");
		if (description.isEmpty()) {
			block(400, "Empty description");
		}

		// Process the transaction
		LOG.info("Transaction ordered by {} from account {} for ${} with reason {}", id, user.getId(), amount, description);
		account.getTransactions().add(new Transaction(user.getIdLong(), Long.parseUnsignedLong(id), amount, 0L,
				OffsetDateTime.now().toEpochSecond(), description, Transaction.TransactionType.PURCHASE));
		account.setBalance(account.getBalance() - amount).save();
		long lottery = (long) (amount * disPay.getLotteryPercentage());
		disPay.setLotteryBalance(disPay.getLotteryBalance() + lottery);
		disPay.setGlobalBalance(disPay.getGlobalBalance() + (amount - lottery));
		return new JSONObject()
				.put("message", "Success")
				.put("new_balance", account.getBalance());
	}

}
