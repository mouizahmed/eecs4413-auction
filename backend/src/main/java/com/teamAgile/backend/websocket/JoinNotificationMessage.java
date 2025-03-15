package com.teamAgile.backend.websocket;

/**
 * Message class for WebSocket notifications about users joining
 */
public class JoinNotificationMessage {
    private String type;
    private String message;
    private String userId;

    // Default constructor needed for Jackson deserialization
    public JoinNotificationMessage() {
    }

    public JoinNotificationMessage(String type, String message, String userId) {
        this.type = type;
        this.message = message;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}