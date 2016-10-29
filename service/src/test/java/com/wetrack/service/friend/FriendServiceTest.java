package com.wetrack.service.friend;

import com.google.gson.reflect.TypeToken;
import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

abstract class FriendServiceTest extends WeTrackServerTestWithUserLoggedIn {

    void assertFriendsNum(User user, int expectedFriendsNum) {
        Response response = get("/users/" + user.getUsername() + "/friends",
                QueryParam.of("token", tokenOf(user)));
        List<User> receivedFriends = assertReceivedEntity(response, 200, new TypeToken<List<User>>(){}.getType());
        assertThat(receivedFriends.size(), is(expectedFriendsNum));
    }

    Response addFriend(User userA, User userB, String tokenOfUserA) {
        return post("/users/" + userA.getUsername() + "/friends/" + userB.getUsername(),
                "", QueryParam.of("token", tokenOfUserA));
    }

}
