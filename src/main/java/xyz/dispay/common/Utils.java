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

import net.dv8tion.jda.api.entities.User;
import xyz.dispay.DisPay;

public class Utils {

	public static User getUserById(long id) {
		try {
			return DisPay.getInstance().getJDA().retrieveUserById(id).complete();
		} catch (Exception ignored) {}
		return null;
	}

	public static User getUserById(String id) {
		try {
			return getUserById(Long.parseUnsignedLong(id));
		} catch (Exception ignored) {}
		return null;
	}

}
