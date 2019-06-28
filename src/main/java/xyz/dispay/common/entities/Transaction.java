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
import xyz.dispay.common.Constants;

import java.time.Instant;
import java.time.OffsetDateTime;

public class Transaction {

    private final long id;
    private final long amount;
    private final long client;
    private final long from;
    private final long timestamp;
    private final String description;
    private final TransactionType type;


    public Transaction(long id, JSONObject object) {
        this.id = id;
        this.amount = object.getLong("a");
        this.client = object.getLong("c");
        this.description = object.getString("d");
        this.from = object.optLong("f", 0);
        this.timestamp = object.getLong("t");
        this.type = TransactionType.fromId(object.getInt("i"));
    }

    public Transaction(long id, long client, long amount, long from, long timestamp, String description,
                       TransactionType type) {
        this.id = id;
        this.amount = amount;
        this.client = client;
        this.description = description;
        this.from = from;
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public long getClient() {
        return client;
    }

    public String getDescription() {
        return description;
    }

    public long getFrom() {
        return from;
    }

    public long getId() {
        return id;
    }

    public OffsetDateTime getTimestamp() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), Constants.OFFSET);
    }

    public TransactionType getType() {
        return type;
    }

    public JSONObject toJSONObject() {
        JSONObject data = new JSONObject()
                .put("a", amount)
                .put("c", client)
                .put("d", description)
                .put("t", timestamp)
                .put("i", type.getId());
        if (from != 0) {
            data.put("f", from);
        }
        return data;
    }

    public enum TransactionType {

        DEPOSIT(0),
        PURCHASE(1),
        UNKNOWN(-1);

        private final int id;

        TransactionType(int id) {
            this.id = id;
        }

        public static TransactionType fromId(int id) {
            for (TransactionType value : values()) {
                if (value.getId() == id) {
                    return value;
                }
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }
    }

}