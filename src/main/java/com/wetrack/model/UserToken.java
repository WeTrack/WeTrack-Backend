package com.wetrack.model;

import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

@Entity(value = "tokens", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field("expireTime"), options = @IndexOptions(expireAfterSeconds = 0)),
        @Index(fields = @Field("username"), options = @IndexOptions(unique = true))
})
public class UserToken extends DbEntity<String> {

    @Id
    private String token;
    private String username;
    private LocalDateTime expireTime;

    public UserToken() {}

    public UserToken(String username, LocalDateTime expireTime) {
        this.username = username;
        this.expireTime = expireTime;
        this.token = HashedIDGenerator.get(username, expireTime.toString());
    }

    public UserToken(String username, long durationAmount, TemporalUnit durationUnit) {
        this(username, LocalDateTime.now().plus(durationAmount, durationUnit));
    }

    public String getToken() {
        return token;
    }
    public String getUsername() {
        return username;
    }
    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    @Override
    public String getId() {
        return token;
    }
    @Override
    public void setId(String id) {
        this.token = id;
    }
}
