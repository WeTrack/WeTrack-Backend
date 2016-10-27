package com.wetrack.service.user;

import com.wetrack.model.User;
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
        UserUpdateService.TokenUserRequest updateRequestEntity =
                new UserUpdateService.TokenUserRequest("", updatedUser);
        Response response = put("/users/" + robertPeng.getUsername(), updateRequestEntity);
        logResponse(response, "user update");
        assertReceivedNonemptyMessage(response, 400); // Bad Request for empty token

        updateRequestEntity.setToken("I am token");
        response = put("/users/" + robertPeng.getUsername(), updateRequestEntity);
        logResponse(response, "user update");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized for incorrect token

        updateRequestEntity.setToken(tokens.get(robertPeng));
        response = put("/users/" + "Whatever", updateRequestEntity);
        logResponse(response, "user update");
        assertReceivedNonemptyMessage(response, 404); // Not Found for not existed user
    }

    @Test
    public void testValidUserUpdate() {
        UserUpdateService.TokenUserRequest updateRequestEntity =
                new UserUpdateService.TokenUserRequest(tokens.get(robertPeng), updatedUser);
        Response response = put("/users/" + robertPeng.getUsername(), updateRequestEntity);

        logResponse(response, "user update");
        assertReceivedNonemptyMessage(response, 200);

        response = get("/users/" + robertPeng.getUsername());
        User userInResponse = assertReceivedEntity(response, 200, User.class);

        assertThat(userInResponse.getEmail(), is(newEmail));
    }

}
