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
import xyz.dispay.common.AccountManager;
import xyz.dispay.common.ClientManager;
import xyz.dispay.common.Constants;
import xyz.dispay.common.RedisManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DisPay {

	private static final Logger LOG = LoggerFactory.getLogger(DisPay.class);

	private static DisPay INSTANCE;

	private AccountManager accountManager;
	private ClientManager clientManager;
	private CommandManager commandManager;
	private RedisManager redisManager;
	private DisPayAPI api;
	private JDA jda;

	private long global;
	private long lottery;
	private long defaultBalance = Constants.DEFAULT_BALANCE;
	private float lotteryPercentage = Constants.DEFAULT_LOTTERY;

	/* Static Methods */

	public static DisPay getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		INSTANCE = new DisPay().run();
	}

	/* Public Methods */

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public DisPayAPI getAPI() {
		return api;
	}

	public ClientManager getClientManager() {
		return clientManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public long getDefaultBalance() {
		return defaultBalance;
	}

	public long getGlobalBalance() {
		return global;
	}

	public JDA getJDA() {
		return jda;
	}

	public long getLotteryBalance() {
		return lottery;
	}

	public float getLotteryPercentage() {
		return lotteryPercentage;
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
		// Configure economy settings
		if (config.has("lottery_percentage") && config.get("lottery_percentage") instanceof Float) {
			float percentage = config.getFloat("lottery_percentage");
			if (percentage < 0 || percentage > 1) {
				LOG.error("lottery_percentage must be between 0 and 1");
			} else {
				lotteryPercentage = percentage;
			}
		}
		if (config.has("starting_balance") && config.get("starting_balance") instanceof Long) {
			defaultBalance = config.getLong("starting_balance");
		}
		accountManager = new AccountManager(this);
		clientManager = new ClientManager(this);
		// Load the pool balances
		String globalBalance = redisManager.get("global");
		if (globalBalance != null) {
			try {
				global = Long.parseUnsignedLong(globalBalance);
			} catch (Exception e) {
				LOG.error("Failed to parse global balance", e);
			}
		}
		String lotteryBalance = redisManager.get("lottery");
		if (lotteryBalance != null) {
			try {
				lottery = Long.parseUnsignedLong(lotteryBalance);
			} catch (Exception e) {
				LOG.error("Failed to parse lottery balance", e);
			}
		}
		// Load the commands
		commandManager = new CommandManager();
		return this;
	}

	public void setGlobalBalance(long balance) {
		global = balance;
		redisManager.set("global", String.valueOf(global));
	}

	public void setLotteryBalance(long balance) {
		lottery = balance;
		redisManager.set("lottery", String.valueOf(lottery));
	}

}
