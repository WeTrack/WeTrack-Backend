package com.wetrack;

import com.wetrack.client.test.ResultResponseTestHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserCreated;
import org.junit.Test;

public class UserExistsTest extends WeTrackIntegrateTestWithUserCreated {
    private ResultResponseTestHelper resultHelper = new ResultResponseTestHelper(200);

    @Test
    public void testExistedUser() {
        client.userExists(username, resultHelper.callback());
        resultHelper.assertSucceeded();
    }

    @Test
    public void testNotExistedUser() {
        client.userExists(username + "Not important", resultHelper.callback());
        resultHelper.assertFailed(404);
    }
}
