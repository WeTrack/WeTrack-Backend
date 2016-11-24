package com.wetrack.model;

import com.google.gson.annotations.SerializedName;
import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(value = "chats", noClassnameStored = true)
@Indexes({
    @Index(fields = @Field("memberNames"))
})
public class Chat extends DbEntity<String> {
    @Id
    private String id;
    private String name;

    @SerializedName("members") private Set<String> memberNames;

    public Chat() {}

    public Chat(String name) {
        this.name = name;
        this.id = HashedIDGenerator.get(name, LocalDateTime.now().toString());
        this.memberNames = new HashSet<>();
    }

    public boolean addMember(User user) {
        return memberNames.add(user.getUsername());
    }

    public boolean addMember(String username) {
        return memberNames.add(username);
    }

    public boolean removeMember(User user) {
        return memberNames.remove(user.getUsername());
    }

    public boolean removeMember(String username) {
        return memberNames.remove(username);
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<String> getMemberNames() {
        return memberNames;
    }
    public void setMemberNames(Set<String> memberNames) {
        this.memberNames = memberNames;
    }
}
