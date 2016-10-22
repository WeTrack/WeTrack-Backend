package com.wetrack.client;

import com.wetrack.client.test.ResultResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserExistsTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private ResultResponseTestHelper resultHelper = new ResultResponseTestHelper();

    @Test
    public void testUserExistsRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, resultHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("HEAD"));
        assertThat(request.getPath(), is("/users/" + username));
    }

    @Test
    public void testUserExistsOnErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(404));
        client.userExists(username, resultHelper.callback(200));

        resultHelper.assertFailed();
    }

    @Test
    public void testUserExistsOnOkResponse() {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, resultHelper.callback(200));

        resultHelper.assertSucceeded();
    }

}
