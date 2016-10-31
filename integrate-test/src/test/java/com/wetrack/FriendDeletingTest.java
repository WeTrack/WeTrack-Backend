package com.wetrack;

import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithFriendAdded;
import org.junit.Test;

public class FriendDeletingTest extends WeTrackIntegrateTestWithFriendAdded {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);

    @Test
    public void testDeletingFriend() {
        client.deleteFriend(robertPeng.getUsername(), tokenOf(robertPeng), windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertFriendNum(robertPeng, 1);
        assertFriendNum(windyChan, 1);
        assertFriendNum(mrDai, 2);
        assertFriendNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendForNotExistUser() {
        client.deleteFriend(notExistEntityId, tokenOf(robertPeng), windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testDeletingFriendWithoutToken() {
        client.deleteFriend(robertPeng.getUsername(), "", windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(400);
        assertFriendNum(robertPeng, 2);
        assertFriendNum(windyChan, 2);
        assertFriendNum(mrDai, 2);
        assertFriendNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendWithInvalidToken() {
        client.deleteFriend(robertPeng.getUsername(), notExistEntityId, windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
        assertFriendNum(robertPeng, 2);
        assertFriendNum(windyChan, 2);
        assertFriendNum(mrDai, 2);
        assertFriendNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendWithOthersToken() {
        client.deleteFriend(robertPeng.getUsername(), tokenOf(windyChan), windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
        assertFriendNum(robertPeng, 2);
        assertFriendNum(windyChan, 2);
        assertFriendNum(mrDai, 2);
        assertFriendNum(littleHearth, 2);
    }

    @Test
    public void testDeletingNotExistFriend() {
        client.deleteFriend(robertPeng.getUsername(), tokenOf(robertPeng), notExistEntityId, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
        assertFriendNum(robertPeng, 2);
        assertFriendNum(windyChan, 2);
        assertFriendNum(mrDai, 2);
        assertFriendNum(littleHearth, 2);
    }
}
