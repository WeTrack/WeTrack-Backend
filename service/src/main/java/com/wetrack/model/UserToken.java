package com.wetrack.model;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.mongodb.morphia.annotations.*;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Random;

@Entity(value = "tokens", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field("expireTime"), options = @IndexOptions(expireAfterSeconds = 0)),
        @Index(fields = @Field("username"), options = @IndexOptions(unique = true))
})
public class UserToken implements DbEntity<String> {
    private static final Random random = new Random(System.currentTimeMillis());

    @Id
    private String token;
    private String username;
    private LocalDateTime expireTime;

    public UserToken(String username, LocalDateTime expireTime) {
        this.username = username;
        this.expireTime = expireTime;
        this.token = new Md5Hash(String.format("%d-%s:%d-%s:%d",
                random.nextInt(), username, random.nextInt(), expireTime, random.nextInt())).toHex();
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
