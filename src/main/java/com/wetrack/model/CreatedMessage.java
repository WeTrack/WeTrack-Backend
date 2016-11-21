package com.wetrack.model;

public class CreatedMessage extends Message {
    private String entityUrl;

    public CreatedMessage() {}

    public CreatedMessage(String message, String entityUrl) {
        super(201, message);
        this.entityUrl = entityUrl;
    }

    public String getEntityUrl() {
        return entityUrl;
    }
    public void setEntityUrl(String entityUrl) {
        this.entityUrl = entityUrl;
    }
}
