package com.wetrack.service.authen;

import com.wetrack.model.User;
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
        // Assert the token is still valid
        tokenVerificationWithAssertion(robertPeng, true);

        // Log out
        Response response = post("/logout", tokens.get(robertPeng));
        assertReceivedEmptyResponse(response, 200);

        // Assert the token has been invalidated
        tokenVerificationWithAssertion(robertPeng, false);
    }

    private void tokenVerificationWithAssertion(User verifiedLoggedInUser, boolean expectedResult) {
        Response response = post("/users/" + verifiedLoggedInUser.getUsername() + "/tokenVerify",
                tokens.get(verifiedLoggedInUser), MediaType.TEXT_PLAIN_TYPE);
        if (expectedResult)
            assertReceivedEntity(response, UserToken.class);
        else
            assertReceivedNonemptyMessage(response, 401);
    }

}
