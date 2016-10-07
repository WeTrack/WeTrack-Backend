package com.wetrack.model;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

@Entity(value = "users", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field(value = "nickname", type = IndexType.TEXT))
})
public class User implements DbEntity<String> {

    @Id
    private String username;
    private String password;
    private String nickname;
    private String iconUrl;

    private UserInfo info;

    public User() {}

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    // Getters and Setters
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getIconUrl() {
        return iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public UserInfo getInfo() {
        return info;
    }
    public void setInfo(UserInfo info) {
        this.info = info;
    }

    @Override
    public String getId() {
        return username;
    }
    @Override
    public void setId(String id) {
        this.username = id;
    }

}
