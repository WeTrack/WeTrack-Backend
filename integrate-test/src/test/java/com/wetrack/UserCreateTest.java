package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.CreatedResponseHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Test;

public class UserCreateTest extends WeTrackIntegrateTest {

    private CreatedResponseHelper messageHelper = new CreatedResponseHelper();

    @Test
    public void testUserCreateWithMessageCallback() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        client.createUser(exampleUser, messageHelper.callback());

        messageHelper.assertReceivedSuccessfulMessage(); // Created
    }

    @Test
    public void testCreateDuplicateUser() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        client.createUser(exampleUser, messageHelper.callback());
        messageHelper.assertReceivedSuccessfulMessage();

        client.createUser(exampleUser, messageHelper.callback()); // Create user with existed username
        messageHelper.assertReceivedFailedMessage(403); // Forbidden
    }

    @Test
    public void testCreateInvalidUser() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        exampleUser.setUsername("");
        client.createUser(exampleUser, messageHelper.callback()); // Create user without username
        messageHelper.assertReceivedFailedMessage(400); // Bad Request

        exampleUser = Utils.loadExampleUser(gson);
        exampleUser.setPassword("");
        client.createUser(exampleUser, messageHelper.callback()); // Create user without password
        messageHelper.assertReceivedFailedMessage(400); // Bad Request
    }

}
