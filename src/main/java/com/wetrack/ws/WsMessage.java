package com.wetrack.ws;

import com.wetrack.model.Notification;

public class WsMessage extends Notification {
    private int statusCode;

    public WsMessage() {}

    public WsMessage(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
