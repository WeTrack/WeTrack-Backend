package com.wetrack.model;

import com.wetrack.util.HashedIDGenerator;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;

import java.time.LocalDateTime;

@Entity("notifications")
@Indexes({
    @Index(fields = @Field(value = "sendTime", type = IndexType.DESC))
})
public abstract class Notification implements DbEntity<String> {

    @Id
    private String id;
    private String content;
    private LocalDateTime sendTime;

    protected Notification() {}

    protected Notification(String content) {
        this.content = content;
        this.sendTime = LocalDateTime.now();
        this.id = HashedIDGenerator.get(content, sendTime.toString());
    }

    protected Notification(String id, String content, LocalDateTime sendTime) {
        this.id = id;
        this.content = content;
        this.sendTime = sendTime;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getSendTime() {
        return sendTime;
    }
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
