package xyz.dispay.common.entities;

import org.json.JSONObject;

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

}
