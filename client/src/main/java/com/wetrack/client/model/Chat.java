package com.wetrack.client.model;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.List;

public class Chat {
    @Expose(serialize = false)
    private String chatId;
    private String name;
    private List<User> members;

    public Chat() {}

    public Chat(String name) {
        this.chatId = "";
        this.name = name;
        this.members = new LinkedList<>();
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<User> getMembers() {
        return members;
    }
    public void setMembers(List<User> members) {
        this.members = members;
    }
}
