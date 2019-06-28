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

import java.awt.*;
import java.time.ZoneOffset;

public class Constants {

	public static final int DEFAULT_PORT = 7777;
	public static final int REDIS_PORT = 6379;
	public static final int REDIS_TIMEOUT = 5000;
	public static final int REDIS_DATABASE = 0;
	public static final long DEFAULT_BALANCE = 1000;
	public static final float DEFAULT_LOTTERY = 0.3f;
	public static final Color BLURPLE = new Color(-9270822);
	public static final String PREFIX = "$";
	public static final ZoneOffset OFFSET = ZoneOffset.of("+00:00");

}
