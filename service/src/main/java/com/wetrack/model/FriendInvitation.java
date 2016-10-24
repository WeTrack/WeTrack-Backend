package com.wetrack.model;

public class FriendInvitation extends Notification {
    private String fromUsername;
    private String toUsername;

    public FriendInvitation() {}

    public FriendInvitation(String fromUsername, String toUsername, String content) {
        super(content);
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
    }

    public String getFromUsername() {
        return fromUsername;
    }
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }
    public String getToUsername() {
        return toUsername;
    }
    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }
}
