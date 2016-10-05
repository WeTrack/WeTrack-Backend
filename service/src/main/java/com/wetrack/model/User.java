package com.wetrack.model;

import org.joda.time.LocalDate;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

@Entity(value = "users", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field(value = "email", type = IndexType.TEXT))
})
public class User implements DbEntity<String> {

    @Id
    private String email;
    private String password;
    private String nickname;
    private String iconUrl;
    private Gender gender;
    private LocalDate birthDate;

    public User() {}

    public User(String email, String nickname, String password) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
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
        return email;
    }
    @Override
    public void setId(String id) {
        this.email = id;
    }

    public enum Gender {
        Male, Female
    }

}
