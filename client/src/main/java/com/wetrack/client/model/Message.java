package com.wetrack.client.model;

public class Message {
    private int statusCode;
    private String message;
    private String documentationUrl;

    public Message() {}

    public Message(int statusCode, String message, String documentationUrl) {
        this.statusCode = statusCode;
        this.message = message;
        this.documentationUrl = documentationUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getDocumentationUrl() {
        return documentationUrl;
    }
    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }
}
