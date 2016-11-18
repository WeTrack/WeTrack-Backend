package com.wetrack;

import com.wetrack.client.model.Chat;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithChatCreated;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChatExitingTest extends WeTrackIntegrateTestWithChatCreated {

    private EntityResponseHelper<List<Chat>> entityHelper = new EntityResponseHelper<>(gson);
    private MessageResponseHelper messageHelper = new MessageResponseHelper(200);

    @Test
    public void testChatExiting() {
        assertChatNum(robertPeng, 2);
        client.exitChat(robertPeng.getUsername(), tokenOf(robertPeng), chatIdOf(robertFamily), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertChatNum(robertPeng, 1);
    }

    @Test
    public void testChatExitingWithEmptyToken() {
        client.exitChat(robertPeng.getUsername(), "", chatIdOf(robertFamily), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testChatExitingWithInvalidToken() {
        client.exitChat(robertPeng.getUsername(), notExistEntityId, chatIdOf(robertFamily), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testChatExitingWithOtherToken() {
        client.exitChat(robertPeng.getUsername(), tokenOf(windyChan), chatIdOf(robertFamily), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testChatExitingWithNotExistChatID() {
        client.exitChat(robertPeng.getUsername(), tokenOf(robertPeng), notExistEntityId, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    private void assertChatNum(User user, int expectedChatNum) {
        client.getUserChatList(user.getUsername(), tokenOf(user), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        assertThat(entityHelper.getReceivedEntity().size(), is(expectedChatNum));
    }

}
