package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("friend_invitations")
@Indexes({
        @Index(fields = @Field("toUsername"))
})
public class FriendInvitation extends Notification {
    @SerializedName("from") private String fromUsername;
    @SerializedName("to") private String toUsername;

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
