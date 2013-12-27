package com.bango.ngintipbot.models;

import org.json.JSONObject;

public class SMSMessage {
    private long id;
    private String body;
    private String number;
    private String timestamp;
    private String person;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public JSONObject toJSON() {
        JSONObject msg = new JSONObject();
        try {
            msg.put("id", id);
            msg.put("number", number);
            msg.put("body", body);
            msg.put("timestamp", timestamp);
            msg.put("person", person);
        } catch(Exception e) {}

        return msg;
    }

    public String toJSONString() {
        return this.toJSON().toString();
    }
}