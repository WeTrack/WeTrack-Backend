package com.wetrack.client;

import com.wetrack.client.test.ResultResponseTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserExistsTest extends ResultResponseTest {

    private String username = "robert-peng";

    @Test
    public void testUserExistsRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("HEAD"));
        assertThat(request.getPath(), is("/users/" + username));
    }

    @Test
    public void testUserExistsOnErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(404));
        client.userExists(username, callback(200));

        assertThat(successful, is(false));
    }

    @Test
    public void testUserExistsOnOkResponse() {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.userExists(username, callback(200));

        assertThat(successful, is(true));
    }

}
