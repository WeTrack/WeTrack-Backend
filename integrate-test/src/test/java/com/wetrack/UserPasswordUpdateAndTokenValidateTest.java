package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Before;
import org.junit.Test;

public class UserPasswordUpdateAndTokenValidateTest extends WeTrackIntegrateTest {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private EntityResponseTestHelper<UserToken> tokenHelper = new EntityResponseTestHelper<>(gson);

    private String username;
    private String password;
    private String newPasword = "Surprise, motherf*cker.";

    private String token;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        User testUser = Utils.loadExampleUser(gson);
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
        client.createUser(testUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        username = testUser.getUsername();
        password = testUser.getPassword();

        // Login
        EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);
        client.userLogin(username, password, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);
        token = entityHelper.getReceivedEntity().getToken();
    }

    @Test
    public void testIncorrectPasswordUpdateWithMessageCallback() {
        client.updateUserPassword(username, password + "whatever", newPasword, messageHelper.callback(200));
        messageHelper.assertReceivedMessage(false);

        client.updateUserPassword(username + "whatever", password, newPasword, messageHelper.callback(200));
        messageHelper.assertReceivedMessage(false);
    }

    @Test
    public void testIncorrectPasswordUpdateWithEntityCallback() {
        client.updateUserPassword(username, password + "whatever", newPasword, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(401);

        client.updateUserPassword(username + "whatever", password, newPasword, entityHelper.callback(200));
        entityHelper.assertReceivedErrorMessage(404);
    }

    @Test
    public void testCorrectPasswordUpdateWithMessageCallback() {
        client.tokenValidate(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedEntity(200);

        client.updateUserPassword(username, password, newPasword, messageHelper.callback(200));
        messageHelper.assertReceivedMessage(true);

        // Assert the old token is invalidated
        client.tokenValidate(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testCorrectPasswordUpdateWithEntityCallback() {
        client.tokenValidate(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedEntity(200);

        client.updateUserPassword(username, password, newPasword, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        // Assert the old token is invalidated
        client.tokenValidate(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedErrorMessage(401);
    }

}
