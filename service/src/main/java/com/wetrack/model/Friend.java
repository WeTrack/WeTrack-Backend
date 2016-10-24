package com.wetrack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.LinkedList;
import java.util.List;

@Entity("friends")
public class Friend implements DbEntity<String> {
    @Id
    private String ownerUsername;

    @Reference(idOnly = true)
    private List<User> friends;

    public Friend() {}

    public Friend(String ownerUsername) {
        this.ownerUsername = ownerUsername;
        this.friends = new LinkedList<>();
    }


    public String getOwnerUsername() {
        return ownerUsername;
    }
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    public List<User> getFriends() {
        return friends;
    }
    public void setFriends(List<User> friends) {
        this.friends = friends;
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
