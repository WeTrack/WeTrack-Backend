package com.wetrack;

import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserCreated;
import org.junit.Test;

public class UserLoginTest extends WeTrackIntegrateTestWithUserCreated {

    private EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testLoginWithCorrectCredential() {
        client.userLogin(robertPeng.getUsername(), robertPeng.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
    }

    @Test
    public void testLoginWithIncorrectCredential() {
        client.userLogin(robertPeng.getUsername(), robertPeng.getPassword() + "Whatever", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
        client.userLogin(robertPeng.getUsername() + "Whatever", robertPeng.getPassword(), entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
    }

}
