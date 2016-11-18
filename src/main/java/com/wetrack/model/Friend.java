package com.wetrack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashSet;
import java.util.Set;

@Entity("friends")
public class Friend extends DbEntity<String> {
    @Id
    private String ownerUsername;

    private Set<String> friendNames;

    public Friend() {}

    public Friend(String ownerUsername) {
        this.ownerUsername = ownerUsername;
        this.friendNames = new HashSet<>();
    }

    public boolean addFriend(User user) {
        return friendNames.add(user.getUsername());
    }

    public boolean addFriend(String username) {
        return friendNames.add(username);
    }

    public boolean removeFriend(User user) {
        return friendNames.remove(user.getUsername());
    }

    public boolean removeFriend(String username) {
        return friendNames.remove(username);
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    public Set<String> getFriendNames() {
        return friendNames;
    }
    public void setFriendNames(Set<String> friendNames) {
        this.friendNames = friendNames;
    }
    @Override
    public String getId() {
        return ownerUsername;
    }
    @Override
    public void setId(String id) {
        this.ownerUsername = id;
    }
}
