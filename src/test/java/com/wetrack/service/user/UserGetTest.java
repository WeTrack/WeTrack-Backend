package com.wetrack.service.user;

import com.wetrack.model.User;
import com.wetrack.test.WeTrackServerTestWithUserCreated;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackServerTestWithUserCreated {

    @Test
    public void testGettingNotExistUser() {
        Response response = get("/users/" + notExistEntityId);
        logResponse(response, "query of not exist user");
        assertReceivedNonemptyMessage(response, 404);
    }

    @Test
    public void testGettingExistUser() {
        Response response = get("/users/" + robertPeng.getUsername());

        logResponse(response, "query of exist user");
        assertReceivedEntity(response, 200, User.class);

        User user = gson.fromJson(response.readEntity(String.class), User.class);
        assertThat(user.getUsername(), is(robertPeng.getUsername()));
        assertThat(user.getNickname(), is(robertPeng.getNickname()));
        assertThat(user.getEmail(), is(robertPeng.getEmail()));
        assertThat(user.getGender(), is(robertPeng.getGender()));
        assertThat(user.getPassword(), nullValue());
    }

    @Test
    public void testHeadingNotExistUser() {
        Response response = head("/users/" + notExistEntityId);
        logResponse(response, "user existence detect of not exist user");
        assertReceivedEmptyResponse(response, 404);
    }

    @Test
    public void testHeadingExistUser() {
        Response response = head("/users/" + robertPeng.getUsername());
        logResponse(response, "user existence detect of exist user");
        assertReceivedEmptyResponse(response, 200);
    }

}
