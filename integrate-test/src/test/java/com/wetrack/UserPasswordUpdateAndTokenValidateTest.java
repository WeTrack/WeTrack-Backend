package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import org.junit.Test;

public class UserPasswordUpdateAndTokenValidateTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper(200);
    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private EntityResponseTestHelper<UserToken> tokenHelper = new EntityResponseTestHelper<>(gson);


    private String newPasword = "Surprise, motherf*cker.";

    @Test
    public void testIncorrectPasswordUpdateWithMessageCallback() {
        client.updateUserPassword(username, password + "whatever", newPasword, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(401);

        client.updateUserPassword(username + "whatever", password, newPasword, messageHelper.callback());
        messageHelper.assertReceivedFailedMessage(404);
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

        client.updateUserPassword(username, password, newPasword, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

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
