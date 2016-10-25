package com.wetrack.test;

import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import org.junit.Before;

public class WeTrackIntegrateTestWithUserLoggedIn extends WeTrackIntegrateTestWithUserCreated {

    protected String token;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);
        client.userLogin(username, password, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        UserToken receivedToken = entityHelper.getReceivedEntity();
        token = receivedToken.getToken();
    }

}
