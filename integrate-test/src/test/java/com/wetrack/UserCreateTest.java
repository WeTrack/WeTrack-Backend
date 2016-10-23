package com.wetrack;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Test;

public class UserCreateTest extends WeTrackIntegrateTest {

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();

    @Test
    public void testUserCreateWithMessageCallback() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        client.createUser(exampleUser, messageHelper.callback(201));

        messageHelper.assertReceivedMessage(true); // Created
    }

    @Test
    public void testUserCreateWithEntityCallback() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        client.createUser(exampleUser, entityHelper.callback(201));

        entityHelper.assertReceivedEntity(201); // Created
    }

    @Test
    public void testCreateDuplicateUser() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        client.createUser(exampleUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        client.createUser(exampleUser, entityHelper.callback(201)); // Create user with existed username
        entityHelper.assertReceivedErrorMessage(403); // Forbidden
    }

    @Test
    public void testCreateInvalidUser() throws Exception {
        User exampleUser = Utils.loadExampleUser(gson);
        exampleUser.setUsername("");
        client.createUser(exampleUser, entityHelper.callback(201)); // Create user without username
        entityHelper.assertReceivedErrorMessage(400); // Bad Request

        exampleUser = Utils.loadExampleUser(gson);
        exampleUser.setPassword("");
        client.createUser(exampleUser, entityHelper.callback(201)); // Create user without password
        entityHelper.assertReceivedErrorMessage(400); // Bad Request
    }

}
