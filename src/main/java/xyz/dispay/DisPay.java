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

package xyz.dispay;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.dispay.api.DisPayAPI;
import xyz.dispay.bot.CommandManager;
import xyz.dispay.bot.Listener;
import xyz.dispay.common.Constants;
import xyz.dispay.common.RedisManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DisPay {

	private static final Logger LOG = LoggerFactory.getLogger(DisPay.class);

	private static DisPay INSTANCE;

	private CommandManager commandManager;
	private RedisManager redisManager;
	private DisPayAPI api;
	private JDA jda;

	/* Static Methods */

	public static DisPay getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		INSTANCE = new DisPay().run();
	}

	/* Public Methods */

	public DisPayAPI getAPI() {
		return api;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public JDA getJDA() {
		return jda;
	}

	public RedisManager getRedisManager() {
		return redisManager;
	}

	/* Private Methods */

	private DisPay run() {
		LOG.info("Initializing DisPay...");
		// Load the configuration file
		File file = new File("config.json");
		JSONObject config;
		try {
			config = new JSONObject(new String(Files.readAllBytes(file.toPath())));
		} catch (IOException e) {
			LOG.error("Failed to read configuration", e);
			System.exit(1);
			return null;
		}
		if (!config.has("token") || !(config.get("token") instanceof String)) {
			LOG.error("The token field is not optional and must be a String");
			System.exit(1);
			return null;
		}
		String token = config.getString("token");
		// Configure redis
		int redisPort = Constants.REDIS_PORT, redisDatabase = Constants.REDIS_DATABASE;
		String redisAddress = "localhost", redisPassword = null;
		if (config.has("redis") && config.get("redis") instanceof JSONObject) {
			JSONObject redis = config.getJSONObject("redis");
			if (redis.has("host") && redis.get("host") instanceof String) {
				redisAddress = redis.getString("host");
			}
			if (redis.has("port") && redis.get("port") instanceof Integer) {
				redisPort = redis.getInt("port");
			}
			if (redis.has("password") && redis.get("password") instanceof String) {
				redisPassword = redis.getString("password");
			}
			if (redis.has("database") && redis.get("database") instanceof Integer) {
				redisDatabase = redis.getInt("database");
			}
		}
		LOG.info("Connecting to redis at {}:{}/{} with password {}", redisAddress, redisPort, redisDatabase, redisPassword);
		redisManager = new RedisManager(redisAddress, redisPort, redisPassword, redisDatabase);
		try {
			// Sign into discord
			jda = new JDABuilder(token.equals("redis") ? redisManager.get("token") : token)
					.addEventListeners(new Listener(this))
					.setActivity(Activity.watching("for transactions | dispay.xyz"))
					.build();
		} catch (Exception e) {
			LOG.error("Failed to login to discord", e);
			System.exit(1);
			return null;
		}
		// Configure the api
		api = new DisPayAPI(this);
		if (config.has("port") && config.get("port") instanceof Integer) {
			api.setPort(config.getInt("port"));
		}
		// Load the commands
		commandManager = new CommandManager();
		return this;
	}

}
