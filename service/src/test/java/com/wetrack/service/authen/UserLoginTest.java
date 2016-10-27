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
        UserLoginService.LoginRequest requestEntity =
                new UserLoginService.LoginRequest(robertPeng.getUsername(),
                        CryptoUtils.md5Digest(robertPeng.getPassword()));
        Response response = post("/login", requestEntity);
        logResponse(response, "user login");
        assertReceivedEntity(response, 200, UserToken.class);
    }

    @Test
    public void testLoginWithInvalidCredential() {
        UserLoginService.LoginRequest requestEntity = new UserLoginService.LoginRequest();
        requestEntity.setUsername(robertPeng.getUsername());

        Response response = post("/login", requestEntity);
        logResponse(response, "user login with empty password");
        assertReceivedNonemptyMessage(response, 400); // Bad Request as password is empty

        requestEntity.setPassword(robertPeng.getPassword());
        response = post("/login", requestEntity);
        logResponse(response, "user login with plain password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized as the credential is incorrect
    }

}
