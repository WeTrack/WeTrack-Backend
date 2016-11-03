package com.wetrack;

import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithChatCreated;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class ChatMemberAddingTest extends WeTrackIntegrateTestWithChatCreated {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testChatMemberAdding() {
        client.addChatMembers(chatIdOf(robertFamily), tokenOf(robertPeng),
                Collections.singletonList(windyChan), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertChatMember(chatIdOf(robertFamily), tokenOf(robertPeng), 3, windyChan);
    }

    @Test
    public void testMultipleMemberAdding() {
        addFriendWithAssertion(robertPeng, littleHearth);
        client.addChatMembers(chatIdOf(robertFamily), tokenOf(robertPeng),
                Arrays.asList(windyChan, littleHearth), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertChatMember(chatIdOf(robertFamily), tokenOf(robertPeng), 4, windyChan, littleHearth);
    }

    @Test
    public void testChatMemberAddingWithoutToken() {
        client.addChatMembers(chatIdOf(robertFamily), "",
                Collections.singletonList(windyChan), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(400);
    }

    @Test
    public void testChatMemberAddingWithInvalidToken() {
        client.addChatMembers(chatIdOf(robertFamily), notExistEntityId,
                Collections.singletonList(windyChan), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testChatMemberAddingWithNonMemberToken() {
        client.addChatMembers(chatIdOf(robertFamily), tokenOf(windyChan),
                Collections.singletonList(windyChan), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testAddingMemberToNotExistChat() {
        client.addChatMembers(notExistEntityId, tokenOf(robertPeng),
                Collections.singletonList(windyChan), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testAddingNonFriendToChat() {
        client.addChatMembers(chatIdOf(robertFamily), tokenOf(robertPeng),
                Collections.singletonList(littleHearth), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(403);
    }

}
