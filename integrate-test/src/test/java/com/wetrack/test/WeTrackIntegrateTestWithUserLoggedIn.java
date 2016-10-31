package com.wetrack.test;

import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

public class WeTrackIntegrateTestWithUserLoggedIn extends WeTrackIntegrateTestWithUserCreated {

    private Map<User, String> tokens;

    protected String token;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        tokens = new HashMap<>();
        EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);
        client.userLogin(robertPeng.getUsername(), robertPeng.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        UserToken receivedToken = entityHelper.getReceivedEntity();
        tokens.put(robertPeng, receivedToken.getToken());
        client.userLogin(windyChan.getUsername(), windyChan.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        receivedToken = entityHelper.getReceivedEntity();
        tokens.put(windyChan, receivedToken.getToken());
        client.userLogin(mrDai.getUsername(), mrDai.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        receivedToken = entityHelper.getReceivedEntity();
        tokens.put(mrDai, receivedToken.getToken());
        client.userLogin(littleHearth.getUsername(), littleHearth.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        receivedToken = entityHelper.getReceivedEntity();
        tokens.put(littleHearth, receivedToken.getToken());

        token = tokens.get(robertPeng);
    }

    protected String tokenOf(String username) {
        return tokenOf(new User(username, null, null));
    }

    protected String tokenOf(User user) {
        return tokens.get(user);
    }

}
