package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "chat_invitations", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field("toUsername"))
})
public class ChatInvitation extends Notification {
    @SerializedName("from") private String fromUsername;
    @SerializedName("to") private String toUsername;
    private String chatId;

    public ChatInvitation() {}

    public ChatInvitation(String fromUsername, String toUsername, String chatId, String content) {
        super(content);
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.chatId = chatId;
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
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
