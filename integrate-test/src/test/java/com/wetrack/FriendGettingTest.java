package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithFriendAdded;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FriendGettingTest extends WeTrackIntegrateTestWithFriendAdded {

    private EntityResponseHelper<List<User>> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testGetFriendList() {
        client.getUserFriendList(robertPeng.getUsername(), tokenOf(robertPeng), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        List<User> receivedFriends = entityHelper.getReceivedEntity();
        assertThat(receivedFriends.size(), is(2));
        assertThat(receivedFriends.stream().map(User::getUsername).collect(Collectors.toList()),
                hasItems(windyChan.getUsername(), mrDai.getUsername()));
    }

    @Test
    public void testGetFriendWithNotExistUsername() {
        client.getUserFriendList(notExistEntityId, tokenOf(robertPeng), entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404); // Not Found for not-exist username
    }

    @Test
    public void testGetFriendListWithoutToken() {
        client.getUserFriendList(robertPeng.getUsername(), "", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(400); // Bad Request for empty token
    }

    @Test
    public void testGetFriendListWithInvalidToken() {
        client.getUserFriendList(robertPeng.getUsername(), notExistEntityId, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401); // Unauthorized for invalid token
    }

    @Test
    public void testGetFriendListWithOthersToken() {
        client.getUserFriendList(robertPeng.getUsername(), tokenOf(windyChan), entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401); // Unauthorized for token owner mismatch
    }

}
