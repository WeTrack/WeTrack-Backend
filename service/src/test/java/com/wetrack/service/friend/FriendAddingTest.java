package com.wetrack.service.friend;

import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class FriendAddingTest extends FriendServiceTest {

    @Test
    public void testAddFriend() {
        Response response = addFriend(robertPeng, windyChan, tokenOf(robertPeng));
        logResponse(response, "adding friend");
        assertReceivedNonemptyMessage(response, 200);
        assertFriendsNum(robertPeng, 1);
    }

    @Test
    public void testAddFriendForNotExistUser() {
        Response response = addFriend(new User(notExistEntityId, null, null), windyChan, tokenOf(robertPeng));
        logResponse(response, "adding friend for not-exist user");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testAddFriendWithoutToken() {
        Response response = addFriend(robertPeng, windyChan, "");
        logResponse(response, "adding friend without token");
        assertReceivedNonemptyMessage(response, 400);
    }

    @Test
    public void testAddFriendWithInvalidToken() {
        Response response = addFriend(robertPeng, windyChan, notExistEntityId);
        logResponse(response, "adding friend with invalid token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testAddFriendWithOthersToken() {
        Response response = addFriend(robertPeng, windyChan, tokenOf(windyChan));
        logResponse(response, "adding friend with others token");
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testAddNotExistFriend() {
        Response response = addFriend(robertPeng, new User(notExistEntityId, null, null), tokenOf(robertPeng));
        logResponse(response, "adding not-exist friend");
        assertReceivedNonemptyMessage(response, 404);
        assertFriendsNum(robertPeng, 0);
    }

    @Test
    public void testAddDuplicateFriend() {
        addFriend(robertPeng, windyChan, tokenOf(robertPeng));

        Response response = addFriend(robertPeng, windyChan, tokenOf(robertPeng));
        logResponse(response, "adding friend");
        assertReceivedNonemptyMessage(response, 200);
        assertFriendsNum(robertPeng, 1);
    }
}
