package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Before;
import org.junit.Test;

public class UserLoginTest extends WeTrackIntegrateTest {

    private EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);
    private String username;
    private String password;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        User testUser = Utils.loadExampleUser(gson);
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
        client.createUser(testUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        username = testUser.getUsername();
        password = testUser.getPassword();
    }

    @Test
    public void testLoginWithCorrectCredential() {
        client.userLogin(username, password, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);
    }

    @Test
    public void testLoginWithIncorrectCredential() {
        client.userLogin(username, password + "Whatever", entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
        client.userLogin(username + "Whatever", password, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);
    }

}
