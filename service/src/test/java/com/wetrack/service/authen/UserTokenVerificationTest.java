package com.wetrack.service.authen;

import com.wetrack.model.UserToken;
import com.wetrack.test.WeTrackServerTestWithUserLoggedIn;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserTokenVerificationTest extends WeTrackServerTestWithUserLoggedIn {

    @Test
    public void testInvalidTokenVerification() {
        Response response = post("/users/" + username + "Whatever" + "/tokenVerify",
                token, MediaType.TEXT_PLAIN_TYPE);
        assertReceivedNonemptyMessage(response, 401);

        response = post("/users/" + username + "/tokenVerify",
                token + "Whatever", MediaType.TEXT_PLAIN_TYPE);
        assertReceivedNonemptyMessage(response, 401);
    }

    @Test
    public void testValidTokenVerification() {
        Response response = post("/users/" + username + "/tokenVerify", token, MediaType.TEXT_PLAIN_TYPE);
        assertReceivedEntity(response, 200, UserToken.class);
    }

}
