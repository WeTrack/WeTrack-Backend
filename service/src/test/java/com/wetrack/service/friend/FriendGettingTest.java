package com.wetrack.service.friend;

import com.wetrack.json.GsonTypes;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FriendGettingTest extends FriendServiceTestWithFriendAdded {

    @Test
    public void testGetFriendList() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", tokenOf(robertPeng)));
        logResponse(response, "getting friend list");
        List<User> receivedFriends = assertReceivedEntity(response, 200, GsonTypes.userListType);
        assertThat(receivedFriends.size(), is(2));
        assertThat(receivedFriends, hasItems(windyChan, mrDai));
    }

    @Test
    public void testGetFriendWithNotExistUsername() {
        Response response = get("/users/" + notExistEntityId + "/friends");
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
                QueryParam.of("token", notExistEntityId));
        logResponse(response, "getting friend list with invalid token");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for invalid token
    }

    @Test
    public void testGetFriendListWithOthersToken() {
        Response response = get("/users/" + robertPeng.getUsername() + "/friends",
                QueryParam.of("token", tokenOf(windyChan)));
        logResponse(response, "getting friend list with other's token");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for token owner mismatch
    }

}
