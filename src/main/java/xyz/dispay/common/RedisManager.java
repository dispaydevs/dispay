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

package xyz.dispay.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

	private final JedisPool pool;

	public RedisManager(String host, int port, String password, int database) {
		this.pool = new JedisPool(new JedisPoolConfig(), host, port, Constants.REDIS_TIMEOUT, password, database);
	}

	public void delete(String key) {
		try (Jedis jedis = getResource()) {
			jedis.del(key);
		}
	}

	public boolean exists(String key) {
		try (Jedis jedis = getResource()) {
			return jedis.exists(key);
		}
	}

	public String get(String key) {
		try (Jedis jedis = getResource()) {
			return jedis.get(key);
		}
	}

	public Jedis getResource() {
		return pool.getResource();
	}

	public void set(String key, String value) {
		try (Jedis jedis = getResource()) {
			jedis.set(key, value);
		}
	}

}
