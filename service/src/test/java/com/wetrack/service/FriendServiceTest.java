package com.wetrack.service;

import com.google.gson.reflect.TypeToken;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FriendServiceTest extends WeTrackServerTestWithUserLoggedIn {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Response response = post("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                "", QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "adding friend");
        assertReceivedEmptyResponse(response, 200);
        assertFriendsNum(1);
    }

    @Test
    public void testAddNotExistFriend() {
        Response response = post("/users/" + robertPeng.getUsername() + "/friends/WHatEver",
                "", QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "adding not-exist friend");
        assertFriendsNum(1);
    }

    @Test
    public void testAddDuplicateFriend() {
        post("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                "", QueryParam.of("token", tokens.get(robertPeng)));

        Response response = post("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                "", QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "adding friend");
        assertReceivedEmptyResponse(response, 200);
        assertFriendsNum(1);
    }

    @Test
    public void testGetFriendList() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "getting friend list");
        List<User> receivedFriends = assertReceivedEntity(response, 200, new TypeToken<List<User>>(){}.getType());
        assertThat(receivedFriends.size(), is(1));
        assertThat(receivedFriends.get(0).getUsername(), is(windyChan.getUsername()));
    }

    @Test
    public void testGetFriendWithNotExistUsername() {
        Response response = get("/users/" + notExistUsername + "/friends");
        logResponse(response, "getting friend list of not exist user");
        assertReceivedNonemptyMessage(response, 404); // Not Found for not-exist username
    }

    @Test
    public void testGetFriendListWithoutToken() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends");
        logResponse(response, "getting friend list without token");
        assertReceivedNonemptyMessage(response, 400); // Bad Request for empty token
    }

    @Test
    public void testGetFriendListWithInvalidToken() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", invalidToken));
        logResponse(response, "getting friend list with invalid token");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for invalid token
    }

    @Test
    public void testGetFriendListWithOthersToken() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", tokens.get(windyChan)));
        logResponse(response, "getting friend list with other's token");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for token owner mismatch
    }

    @Test
    public void testDeletingFriend() {
        Response response = delete("/users/" + robertPeng.getUsername() + "/friends/" + windyChan.getUsername(),
                QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "deleting friend");
        assertReceivedEmptyResponse(response, 200);
        assertFriendsNum(0);
    }

    private void assertFriendsNum(int expectedFriendsNum) {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", tokens.get(robertPeng)));
        logResponse(response, "getting friend list");
        List<User> receivedFriends = assertReceivedEntity(response, 200, new TypeToken<List<User>>(){}.getType());
        assertThat(receivedFriends.size(), is(expectedFriendsNum));
    }
}
