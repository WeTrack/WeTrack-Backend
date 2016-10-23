package com.wetrack.service.user;

import com.wetrack.model.User;
import com.wetrack.test.WeTrackServerTest;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackServerTest {

    @Test
    public void testGettingNotExistUser() {
        Response response = get("/users/BlahBlahNotExist");
        logResponse(response, "query of not exist user");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testGettingExistUser() {
        createUserWithAssertion();

        Response response = get("/users/" + username);

        logResponse(response, "query of exist user");
        assertReceivedEntity(response, 200, User.class);

        User user = gson.fromJson(response.readEntity(String.class), User.class);
        assertThat(user.getUsername(), is(username));
        assertThat(user.getNickname(), is(nickname));
        assertThat(user.getPassword().isEmpty(), is(true));
    }

    @Test
    public void testHeadingNotExistUser() {
        Response response = head("/users/" + username);
        logResponse(response, "user existence detect of not exist user");
        assertReceivedEmptyResponse(response, 404);
    }

    @Test
    public void testHeadingExistUser() {
        createUserWithAssertion();

        Response response = head("/users/" + username);
        logResponse(response, "user existence detect of exist user");
        assertReceivedEmptyResponse(response, 200);
    }

}
