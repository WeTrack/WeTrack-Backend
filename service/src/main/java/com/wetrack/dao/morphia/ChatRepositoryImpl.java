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
    public boolean chatExists(String chatId) {
        return createQuery().field("_id").equal(chatId).countAll() > 0;
    }

    @Override
    public List<Chat> getChatListByUsername(String username) {
        return createQuery().field("memberNames").hasThisOne(username).asList();
    }

    @Override
    public boolean isMember(String chatId, String username) {
        return createQuery().field("_id").equal(chatId).field("memberNames").hasThisOne(username).countAll() > 0;
    }
}
