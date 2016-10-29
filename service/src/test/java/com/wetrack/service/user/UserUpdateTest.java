package com.wetrack.service.user;

import com.wetrack.model.User;
import com.wetrack.test.QueryParam;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserUpdateTest extends WeTrackServerTestWithUserLoggedIn {

    private User updatedUser;
    private String newEmail = "robert.peng@hotmail.com";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Response response = get("/users/" + robertPeng.getUsername());
        User userInResponse = assertReceivedEntity(response, 200, User.class);
        userInResponse.setEmail(newEmail);
        userInResponse.setBirthDate(null);
        // FIXME The Jackson serializer Jersey Test uses cannot correctly handle java.time classes,
        // FIXME it keeps on serializing them as Bean
        updatedUser = userInResponse;
    }

    @Test
    public void testInvalidUserUpdate() {
        Response response = updateUser(robertPeng.getUsername(), "");
        logResponse(response, "user update with empty token");
        assertReceivedNonemptyMessage(response, 400); // Bad Request for empty token

        response = updateUser(robertPeng.getUsername(), "I\'m a token.");
        logResponse(response, "user update with invalid token");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for incorrect token

        response = put("/users/" + "Whatever", tokenOf(robertPeng));
        logResponse(response, "user update for not-exist user");
        assertReceivedNonemptyMessage(response, 404); // Not Found for not existed user
    }

    @Test
    public void testValidUserUpdate() {
        Response response = updateUser(robertPeng.getUsername(), tokenOf(robertPeng));

        logResponse(response, "user update");
        assertReceivedNonemptyMessage(response, 200);

        response = get("/users/" + robertPeng.getUsername());
        User userInResponse = assertReceivedEntity(response, 200, User.class);

        assertThat(userInResponse.getEmail(), is(newEmail));
    }

    private Response updateUser(String username, String token) {
        return put("/users/" + robertPeng.getUsername(), updatedUser, QueryParam.of("token", token));
    }

}
