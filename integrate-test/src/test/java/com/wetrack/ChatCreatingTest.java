package com.wetrack;

import com.wetrack.client.model.Chat;
import com.wetrack.client.test.CreatedResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithFriendAdded;
import org.junit.Before;
import org.junit.Test;

public class ChatCreatingTest extends WeTrackIntegrateTestWithFriendAdded {

    private Chat testChat;

    private CreatedResponseHelper messageHelper = new CreatedResponseHelper();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testChat = new Chat("Robert & Windy");
        testChat.getMemberNames().add(windyChan.getUsername());
    }

    @Test
    public void testChatCreating() {
        client.createChat(tokenOf(robertPeng), testChat, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
    }

    @Test
    public void testChatCreatingWithoutToken() {
        client.createChat("", testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(400);
    }

    @Test
    public void testChatCreatingWithInvalidToken() {
        client.createChat(notExistEntityId, testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testChatCreatingWithNotExistUser() {
        testChat.getMemberNames().add("BlahBlahBlahNotExist");
        client.createChat(tokenOf(robertPeng), testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testChatCreatingWithNotFriend() {
        testChat.getMemberNames().add(littleHearth.getUsername());
        client.createChat(tokenOf(robertPeng), testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(403);
    }

}
