package com.wetrack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("messages")
@Indexes(
        @Index(fields = @Field("chatId"))
)
public class ChatMessage extends Notification {
    private String chatId;
    private String fromUsername;

    public ChatMessage() {}

    public ChatMessage(String fromUsername, String chatId, String content) {
        super(content);
        this.fromUsername = fromUsername;
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getFromUsername() {
        return fromUsername;
    }
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }
}
