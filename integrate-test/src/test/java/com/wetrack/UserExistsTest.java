package com.wetrack;

import com.wetrack.client.test.ResultResponseHelper;
import com.wetrack.test.WeTrackIntegrateTestWithUserCreated;
import org.junit.Test;

public class UserExistsTest extends WeTrackIntegrateTestWithUserCreated {
    private ResultResponseHelper resultHelper = new ResultResponseHelper(200);

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
