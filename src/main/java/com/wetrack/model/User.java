package com.wetrack.model;

import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.time.LocalDate;

@Entity(value = "users", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field(value = "nickname", type = IndexType.TEXT))
})
public class User extends DbEntity<String> {

    @Id
    private String username = "";
    private String password = null;
    private String nickname = "";
    private String email = "";
    private Gender gender = Gender.Male;
    private LocalDate birthDate;

    public User() {}

    public User(String username, String password, String nickname) {
        this();
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof User)
            return username.equals(((User) other).getUsername());
        return false;
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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String getId() {
        return username;
    }
    @Override
    public void setId(String id) {
        this.username = id;
    }

    public enum Gender {
        Male, Female
    }
}
