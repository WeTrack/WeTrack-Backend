package com.wetrack.model;

import com.google.gson.annotations.Expose;
import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity("chats")
@Indexes({
    @Index(fields = @Field("ownerUsername")),
    @Index(fields = @Field("members"))
})
public class Chat implements DbEntity<String> {
    @Id
    private String id;
    private String chatName;

    @Reference(idOnly = true, lazy = true)
    @Expose(serialize = false, deserialize = false)
    private Set<User> members;

    public Chat() {}

    public Chat(String chatName) {
        this.chatName = chatName;
        this.id = HashedIDGenerator.get(chatName, LocalDateTime.now().toString());
        this.members = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getChatName() {
        return chatName;
    }
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
    public Set<User> getMembers() {
        return members;
    }
    public void setMembers(Set<User> members) {
        this.members = members;
    }
}
