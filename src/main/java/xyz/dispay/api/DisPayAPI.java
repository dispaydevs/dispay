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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import xyz.dispay.DisPay;
import xyz.dispay.common.Constants;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

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
			get("/purchase", this::purchase);
		});

	}

	/* Utility Methods */

	private void badRequest() {
		block(400, "Bad request");
	}

	private void block(int status, String message) {
		halt(status, new JSONObject().put("message", message).toString());
	}

	private String checkAuthorization(String token) {
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
		String verify = disPay.getRedisManager().get(id);
		if (verify == null || !verify.equals(token)) {
			unauthorized();
		}
		return id;
	}

	private void unauthorized() {
		block(401, "Unauthorized");
	}

	/* Endpoints */

	private Object landing(Request request, Response response) {
		return new JSONObject()
				.put("message", "Welcome to DisPay");
	}

	private Object purchase(Request request, Response response) {
		block(501, "Not Implemented");
		return null;
	}

}
