package com.wetrack.service.friend;

import com.wetrack.test.QueryParam;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class FriendDeletingTest extends FriendServiceTestWithFriendAdded {

    @Test
    public void testDeletingFriend() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "deleting friend");
        assertReceivedNonemptyMessage(response, 200);
        assertFriendsNum(robertPeng, 1);
        assertFriendsNum(windyChan, 1);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendForNotExistUser() {
        Response response = delete("/users/" + notExistEntityId + "/friends/" + windyChan.getUsername(),
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "deleting friend for not-exist user");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testDeletingFriendWithoutToken() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername());
        logResponse(response, "deleting friend without token");
        assertReceivedNonemptyMessage(response, 400);
        assertFriendsNum(robertPeng, 2);
        assertFriendsNum(windyChan, 2);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendWithInvalidToken() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                QueryParam.of("token", notExistEntityId));
        logResponse(response, "deleting friend with invalid token");
        assertReceivedNonemptyMessage(response, 401);
        assertFriendsNum(robertPeng, 2);
        assertFriendsNum(windyChan, 2);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }

    @Test
    public void testDeletingFriendWithOthersToken() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                QueryParam.of("token", tokenOf(windyChan)));
        logResponse(response, "deleting friend with other's token");
        assertReceivedNonemptyMessage(response, 401);
        assertFriendsNum(robertPeng, 2);
        assertFriendsNum(windyChan, 2);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }

    @Test
    public void testDeletingNotExistFriend() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + littleHearth.getUsername(),
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "deleting not-exist friend");
        assertReceivedNonemptyMessage(response, 404);
        assertFriendsNum(robertPeng, 2);
        assertFriendsNum(windyChan, 2);
        assertFriendsNum(mrDai, 2);
        assertFriendsNum(littleHearth, 2);
    }
}
