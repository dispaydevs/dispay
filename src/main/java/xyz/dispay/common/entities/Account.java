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

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.dispay.DisPay;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.List;


public class Account {

    private final long id;
    private final List<Transaction> transactions;
    private long balance;

    public Account(long id, JSONObject data) {
        this.id = id;
        this.balance = data.optLong("b", 250);
        JSONArray t = data.optJSONArray("t");
        this.transactions = new ArrayList<>(t == null ? 10 : t.length());
        if (t != null) {
            t.forEach(object -> this.transactions.add(new Transaction(id, (JSONObject) object)));
        }
    }

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public long getBalance() {
        return balance;
    }

    public long getId() {
        return id;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public JSONObject toJSONObject() {
        return new JSONObject()
                .put("b", balance)
                .put("t", transactions.stream().map(Transaction::toJSONObject).toArray());
    }

    public void save() {
        DisPay.getInstance().getAccountManager().save(id);
    }

    @CheckReturnValue
    public Account setBalance(long balance) {
        this.balance = balance;
        return this;
    }

}
