package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.test.MessageResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserLoggedIn;
import org.junit.Test;

public class UserPasswordUpdateAndTokenValidateTest extends WeTrackIntegrateTestWithUserLoggedIn {

    private MessageResponseHelper messageHelper = new MessageResponseHelper(200);
    private EntityResponseHelper<Message> entityHelper = new EntityResponseHelper<>(gson);
    private EntityResponseHelper<UserToken> tokenHelper = new EntityResponseHelper<>(gson);

    private String username = robertPeng.getUsername();
    private String password = robertPeng.getPassword();
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
        client.tokenVerify(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedEntity(200);

        client.updateUserPassword(username, password, newPasword, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        // Assert the old token is invalidated
        client.tokenVerify(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testCorrectPasswordUpdateWithEntityCallback() {
        client.tokenVerify(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedEntity(200);

        client.updateUserPassword(username, password, newPasword, entityHelper.callback(200));
        entityHelper.assertReceivedEntity(200);

        // Assert the old token is invalidated
        client.tokenVerify(username, token, tokenHelper.callback(200));
        tokenHelper.assertReceivedErrorMessage(401);
    }

}
