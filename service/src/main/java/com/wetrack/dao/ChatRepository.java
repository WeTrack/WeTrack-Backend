package com.wetrack.dao;

import com.wetrack.model.Chat;

import java.util.List;

public interface ChatRepository extends Repository<String, Chat> {

    List<Chat> getChatListByUsername(String username);

    boolean isMember(String chatId, String username);

}
