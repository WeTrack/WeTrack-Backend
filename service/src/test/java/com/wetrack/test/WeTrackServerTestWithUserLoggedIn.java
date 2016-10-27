package com.wetrack.test;

import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import com.wetrack.service.authen.UserLoginService;
import com.wetrack.util.CryptoUtils;
import org.junit.Before;

import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class WeTrackServerTestWithUserLoggedIn extends WeTrackServerTestWithUserCreated {

    protected Map<User, String> tokens;
    protected String invalidToken = "1234567890abcdef1234567890abcdef";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        tokens = new HashMap<>();
        String token = loginUserWithAssertion(robertPeng);
        tokens.put(robertPeng, token);
        token = loginUserWithAssertion(windyChan);
        tokens.put(windyChan, token);
        token = loginUserWithAssertion(mrDai);
        tokens.put(mrDai, token);
        token = loginUserWithAssertion(littleHearth);
        tokens.put(littleHearth, token);
    }

    protected String loginUserWithAssertion(User user) {
        UserLoginService.LoginRequest requestEntity =
                new UserLoginService.LoginRequest(user.getUsername(), CryptoUtils.md5Digest(user.getPassword()));

        Response response = post("/login", requestEntity);
        assertThat(response.getStatus(), is(200));

        String responseBody = response.readEntity(String.class);
        UserToken token = gson.fromJson(responseBody, UserToken.class);
        return token.getToken();
    }

}
