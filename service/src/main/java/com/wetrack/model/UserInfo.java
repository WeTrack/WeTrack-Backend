package com.wetrack.model;

import org.joda.time.LocalDate;
import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class UserInfo {
    private String email;
    private Gender gender;
    private LocalDate birthDate;

    public UserInfo() {}

    public UserInfo(String email, Gender gender, LocalDate birthDate) {
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
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

    public enum Gender {
        Male, Female
    }
}
