package com.wetrack.ws;

import java.time.LocalDateTime;

/**
 * ACK message for a received {@link com.wetrack.model.ChatMessage ChatMessage}.
 */
public class ChatMessageAck {
    private String id;
    private LocalDateTime actualSendTime;

    public ChatMessageAck(String id, LocalDateTime actualSendTime) {
        this.id = id;
        this.actualSendTime = actualSendTime;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public LocalDateTime getActualSendTime() { return actualSendTime; }

    public void setActualSendTime(LocalDateTime actualSendTime) { this.actualSendTime = actualSendTime; }
}
