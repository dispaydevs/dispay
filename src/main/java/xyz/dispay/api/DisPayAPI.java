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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import xyz.dispay.DisPay;
import xyz.dispay.Constants;

import static spark.Spark.*;

public class DisPayAPI {

	private static final Logger LOG = LoggerFactory.getLogger(DisPayAPI.class);

	private final DisPay disPay;
	private int port = Constants.DEFAULT_PORT;

	/* Public Methods */

	public DisPayAPI(DisPay disPay) {
		this.disPay = disPay;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() {

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
		});

	}

	/* Utility Methods */

	private void badRequest() {
		block(400, "Bad request");
	}

	private void block(int status, String message) {
		halt(status, new JSONObject().put("message", message).toString());
	}

	private void unauthorized() {
		block(401, "Unauthorized");
	}

	/* Endpoints */

	private Object landing(Request request, Response response) {
		return new JSONObject()
				.put("message", "Welcome to DisPay");
	}

}
