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

package xyz.dispay.common.entities;

import org.json.JSONObject;
import xyz.dispay.DisPay;

public class Client {

    private final long id;
    private final long owner;
    private final String token;

    public Client(long id, JSONObject data) {
        this.id = id;
        this.owner = data.getLong("o");
        this.token = data.getString("t");
    }

    public Client(long id, long owner, String token) {
        this.id = id;
        this.owner = owner;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public long getOwner() {
        return owner;
    }

    public String getToken() {
        return token;
    }

    public JSONObject toJSONObject() {
        return new JSONObject()
                .put("owner", owner)
                .put("token", token);
    }

    public void save() {
        DisPay.getInstance().getClientManager().save(id);
    }

}
