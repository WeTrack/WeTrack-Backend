package com.wetrack.model;

import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity("chats")
@Indexes({
    @Index(fields = @Field("members"))
})
public class Chat implements DbEntity<String> {
    @Id
    private String id;
    private String chatName;

    @Reference(idOnly = true, lazy = true)
    private List<User> members;

    public Chat() {}

    public Chat(String chatName) {
        this.chatName = chatName;
        this.id = HashedIDGenerator.get(chatName, LocalDateTime.now().toString());
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
    public List<User> getMembers() {
        return members;
    }
    public void setMembers(List<User> members) {
        this.members = members;
    }
}
