package com.wetrack.dao.morphia;

import com.wetrack.dao.ChatRepository;
import com.wetrack.model.Chat;

import java.util.List;

public class ChatRepositoryImpl extends MorphiaRepository<String, Chat> implements ChatRepository {
    @Override
    protected Class<Chat> getEntityClass() {
        return Chat.class;
    }

    @Override
    public List<Chat> getChatListByUsername(String username) {
        return createQuery().field("members").hasThisOne(username).asList();
    }

    @Override
    public boolean isMember(String chatId, String username) {
        return createQuery().field("_id").equal(chatId).field("members").hasThisOne(username).countAll() > 0;
    }
}
