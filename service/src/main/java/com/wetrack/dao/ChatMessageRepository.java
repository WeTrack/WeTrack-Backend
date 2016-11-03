package com.wetrack.dao;

import com.wetrack.model.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends Repository<String, ChatMessage> {

    List<ChatMessage> getChatMessages(String chatId, LocalDateTime since, LocalDateTime before);

    List<ChatMessage> getChatMessages(String chatId, LocalDateTime before, int limit);

}
