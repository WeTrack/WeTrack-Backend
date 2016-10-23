package com.wetrack;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.ResultResponseTestHelper;
import com.wetrack.test.Utils;
import com.wetrack.test.WeTrackIntegrateTest;
import org.junit.Before;
import org.junit.Test;

public class UserExistsTest extends WeTrackIntegrateTest {
    private ResultResponseTestHelper resultHelper = new ResultResponseTestHelper();

    private String username;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        User testUser = Utils.loadExampleUser(gson);
        MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
        client.createUser(testUser, messageHelper.callback(201));
        messageHelper.assertReceivedMessage(true);

        username = testUser.getUsername();
    }

    @Test
    public void testExistedUser() {
        client.userExists(username, resultHelper.callback(200));
        resultHelper.assertSucceeded();
    }

    @Test
    public void testNotExistedUser() {
        client.userExists(username + "Not important", resultHelper.callback(200));
        resultHelper.assertFailed();
    }
}
