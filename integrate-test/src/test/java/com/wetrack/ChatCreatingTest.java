package com.wetrack;

import com.google.gson.JsonObject;
import com.wetrack.client.model.Chat;
import com.wetrack.client.test.CreatedResponseTestHelper;
import com.wetrack.client.model.User;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackIntegrateTestWithFriendAdded;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatCreatingTest extends WeTrackIntegrateTestWithFriendAdded {

    private Chat testChat;

    private CreatedResponseTestHelper messageHelper = new CreatedResponseTestHelper();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testChat = new Chat("Robert & Windy");
        testChat.getMembers().add(windyChan);
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
        testChat.getMembers().add(new User("BlahBlahBlahNotExist", null, null));
        client.createChat(tokenOf(robertPeng), testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testChatCreatingWithNotFriend() {
        testChat.getMembers().add(littleHearth);
        client.createChat(tokenOf(robertPeng), testChat, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(403);
    }

}
