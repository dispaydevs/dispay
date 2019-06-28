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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.json.JSONObject;
import xyz.dispay.DisPay;
import xyz.dispay.common.entities.Account;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AccountManager {

	private final LoadingCache<Long, Account> cache;
	private final DisPay disPay;

	public AccountManager(DisPay disPay) {
		this.disPay = disPay;
		this.cache = CacheBuilder.newBuilder()
				.expireAfterAccess(1, TimeUnit.HOURS)
				.build(new CacheLoader<>() {
					@Override
					public Account load(@Nonnull Long key) throws Exception {
						if (isCached(key)) {
							return cache.get(key);
						}
						return new Account(key, provideData(key));
					}
				});
	}

	public Account getAccount(long key) {
		try {
			return cache.get(key);
		} catch (ExecutionException ignored) {}
		// It is impossible for this to return null
		return null;
	}

	public Account getAccount(String key) {
		try {
			return getAccount(Long.parseUnsignedLong(key));
		} catch (Exception ignored) {}
		return null;
	}

	public boolean isCached(long key) {
		return cache.asMap().containsKey(key);
	}

	private JSONObject provideData(long key) {
		String data = disPay.getRedisManager().get(String.valueOf(key));
		return data == null ? new JSONObject() : new JSONObject(data);
	}

	public void save(long key) {
		try {
			disPay.getRedisManager().set(String.valueOf(key), cache.get(key).toJSONObject().toString());
		} catch (Exception ignored) {}
	}

}
