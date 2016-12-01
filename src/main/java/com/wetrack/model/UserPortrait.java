package com.wetrack.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.LocalDateTime;

@Entity(value = "portraits", noClassnameStored = true)
public class UserPortrait extends DbEntity<String> {

    @Id private String username;
    private LocalDateTime updatedAt;
    private PictureType type;

    public UserPortrait() {}

    public UserPortrait(String username, String fileName) {
        this.username = username;
        updatedAt = LocalDateTime.now();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            type = PictureType.JPEG;
        else if (fileName.endsWith(".png"))
            type = PictureType.PNG;
        else if (fileName.endsWith(".gif"))
            type = PictureType.GIF;
        else
            throw new IllegalArgumentException("The given file name `" + fileName + "` has an unsupported suffix.");
    }

    public UserPortrait(String username, PictureType type) {
        this.username = username;
        this.type = type;
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String getId() { return username; }

    @Override
    public void setId(String id) { this.username = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public PictureType getType() { return type; }
    public void setType(PictureType type) { this.type = type; }
}
