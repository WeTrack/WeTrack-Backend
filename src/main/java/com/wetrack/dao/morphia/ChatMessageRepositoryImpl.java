package com.wetrack.dao.morphia;

import com.wetrack.dao.ChatMessageRepository;
import com.wetrack.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageRepositoryImpl extends MorphiaRepository<String, ChatMessage> implements ChatMessageRepository {
    @Override
    public List<ChatMessage> getChatMessages(String chatId, LocalDateTime since, LocalDateTime before) {
        return createQuery().field("chatId").equal(chatId)
                .field("sendTime").greaterThanOrEq(since)
                .field("sendTime").lessThan(before).asList();
    }

    @Override
    public List<ChatMessage> getChatMessages(String chatId, LocalDateTime before, int limit) {
        return createQuery().field("chatId").equal(chatId)
                .field("sendTime").lessThan(before).order("-sendTime")
                .limit(limit).asList();
    }

    @Override
    protected Class<ChatMessage> getEntityClass() {
        return ChatMessage.class;
    }
}
