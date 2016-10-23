package com.wetrack.model;

import com.google.gson.annotations.SerializedName;

public class CreatedMessage extends Message {
    @SerializedName("entity_url")
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
