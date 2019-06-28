package xyz.dispay.common.entities;

import org.json.JSONObject;
import xyz.dispay.common.Constants;

import java.time.Instant;
import java.time.OffsetDateTime;

public class Transaction {

    private final long id;
    private final long amount;
    private final long timestamp;
    private final String description;


    public Transaction(long id, JSONObject object) {
        this.id = id;
        this.timestamp = object.getLong("t");
        this.amount = object.getLong("a");
        this.description = object.getString("d");
    }

    public Transaction(long id, long amount, long timestamp, String description) {
        this.amount = amount;
        this.description = description;
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public OffsetDateTime getTimestamp() {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), Constants.OFFSET);
    }

    public JSONObject toJSONObject() {
        return new JSONObject()
                .put("a", amount)
                .put("d", description)
                .put("t", timestamp);
    }

}