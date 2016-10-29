package com.wetrack.service.authen;

import com.wetrack.model.UserToken;
import com.wetrack.test.WeTrackServerTest;
import com.wetrack.test.WeTrackServerTestWithUserCreated;
import com.wetrack.util.CryptoUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class UserLoginTest extends WeTrackServerTestWithUserCreated {

    @Test
    public void testLoginWithCorrectCredential() {
        Response response = userLogin(robertPeng.getUsername(), robertPeng.getPassword());
        logResponse(response, "user login");
        assertReceivedEntity(response, 200, UserToken.class);
    }

    @Test
    public void testLoginWithInvalidCredential() {
        Response response = userLogin(robertPeng.getUsername(), "");
        logResponse(response, "user login with empty password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized as the credential is incorrect

        response = userLogin(robertPeng.getUsername(), windyChan.getPassword());
        logResponse(response, "user login with incorrect password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized as the credential is incorrect
    }

}
