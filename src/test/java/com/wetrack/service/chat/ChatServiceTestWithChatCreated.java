package com.wetrack.service.chat;

import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

abstract class ChatServiceTestWithChatCreated extends ChatServiceTest {
    private Map<String, String> chatIds;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        chatIds = new HashMap<>();

        String chatId = createChatWithAssertion(robertAndWindy, tokenOf(robertPeng), windyChan);
        chatIds.put(robertAndWindy, chatId);
        chatId = createChatWithAssertion(mrDaiAndLittleHearth, tokenOf(mrDai), littleHearth);
        chatIds.put(mrDaiAndLittleHearth, chatId);
        chatId = createChatWithAssertion(robertFamily, tokenOf(robertPeng), mrDai);
        chatIds.put(robertFamily, chatId);
        chatId = createChatWithAssertion(windyFamily, tokenOf(windyChan), littleHearth);
        chatIds.put(windyFamily, chatId);
    }

    protected String chatIdOf(String chatName) {
        return chatIds.get(chatName);
    }

}
