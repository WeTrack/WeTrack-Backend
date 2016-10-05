package com.wetrack.model;

import org.joda.time.LocalDate;
import org.mongodb.morphia.annotations.Id;

@org.mongodb.morphia.annotations.Entity
public class User implements Entity<String> {

    @Id
    private String email;
    private String password;
    private String nickname;
    private String iconUrl;
    private Gender gender;
    private LocalDate birthDate;

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
