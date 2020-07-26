package com.example.household;

public class Message {
    private String content;
    private String clientName;
    private String timestamp;
    private boolean belongsToCurrentUser;

    public Message(String content, String clientName, String timestamp, boolean belongsToCurrentUser) {
        this.content = content;
        this.clientName = clientName;
        this.timestamp = timestamp;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getContent() {
        return this.content;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public boolean isBelongsToCurrentUser() {
        return this.belongsToCurrentUser;
    }
}
