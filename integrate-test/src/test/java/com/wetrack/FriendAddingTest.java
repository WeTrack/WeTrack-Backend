package com.wetrack;

import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithFriendAdded;
import org.junit.Test;

public class FriendAddingTest extends WeTrackIntegrateTestWithFriendAdded {

    MessageResponseHelper messageHelper = new MessageResponseHelper(200);

    @Test
    public void testAddFriend() {
        assertFriendNum(robertPeng, 2);
        client.addFriend(robertPeng.getUsername(), tokenOf(robertPeng),
                littleHearth.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertFriendNum(robertPeng, 3);
    }

    @Test
    public void testAddFriendForNotExistUser() {
        client.addFriend(notExistEntityId, tokenOf(robertPeng),
            windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testAddFriendWithoutToken() {
        client.addFriend(robertPeng.getUsername(), "", windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(400);
    }

    @Test
    public void testAddFriendWithInvalidToken() {
        client.addFriend(robertPeng.getUsername(), notExistEntityId,
                windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testAddFriendWithOthersToken() {
        client.addFriend(robertPeng.getUsername(), tokenOf(windyChan),
                windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);
    }

    @Test
    public void testAddNotExistFriend() {
        client.addFriend(robertPeng.getUsername(), tokenOf(robertPeng),
                notExistEntityId, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
    }

    @Test
    public void testAddDuplicateFriend() {
        client.addFriend(robertPeng.getUsername(), tokenOf(robertPeng),
                windyChan.getUsername(), messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();
        assertFriendNum(robertPeng, 2);
    }

}
