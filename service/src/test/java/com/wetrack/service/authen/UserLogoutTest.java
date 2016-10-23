package com.wetrack.service.authen;

import com.wetrack.model.UserToken;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserLogoutTest extends WeTrackServerTestWithUserLoggedIn {

    @Test
    public void testLogoutWithInvalidToken() {
        Response response = post("/logout", "");
        assertReceivedNonemptyMessage(response, 400); // Bad Request as given token is empty
    }

    @Test
    public void testLogoutWithValidToken() {
        Response response = post("/users/" + username + "/tokenVerify", token, MediaType.TEXT_PLAIN_TYPE);
        assertReceivedEntity(response, 200, UserToken.class);

        response = post("/logout", token);
        assertReceivedEmptyResponse(response, 200);

        response = post("/users/" + username + "/tokenVerify", token, MediaType.TEXT_PLAIN_TYPE);
        assertReceivedNonemptyMessage(response, 401);
    }

}
