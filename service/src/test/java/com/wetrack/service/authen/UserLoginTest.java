package com.wetrack.service.authen;

import com.wetrack.model.UserToken;
import com.wetrack.test.WeTrackServerTest;
import com.wetrack.util.CryptoUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class UserLoginTest extends WeTrackServerTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createUserWithAssertion();
    }

    @Test
    public void testLoginWithCorrectCredential() {
        UserLoginService.LoginRequest requestEntity =
                new UserLoginService.LoginRequest(username, CryptoUtils.md5Digest(password));
        Response response = post("/login", requestEntity);
        logResponse(response, "user login");
        assertReceivedEntity(response, 200, UserToken.class);
    }

    @Test
    public void testLoginWithInvalidCredential() {
        UserLoginService.LoginRequest requestEntity = new UserLoginService.LoginRequest();
        requestEntity.setUsername(username);

        Response response = post("/login", requestEntity);
        logResponse(response, "user login with empty password");
        assertReceivedNonemptyMessage(response, 400); // Bad Request as password is empty

        requestEntity.setPassword(password);
        response = post("/login", requestEntity);
        logResponse(response, "user login with plain password");
        assertReceivedNonemptyMessage(response, 401); // Unauthorized as the credential is incorrect
    }

}
