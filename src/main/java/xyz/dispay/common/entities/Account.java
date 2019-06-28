package xyz.dispay.common.entities;

import org.json.JSONArray;
import org.json.JSONObject;

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
        t.forEach(object -> this.transactions.add(new Transaction(id, (JSONObject) object)));
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

    public Account setBalance(long balance) {
        this.balance = balance;
        return this;
    }

}
