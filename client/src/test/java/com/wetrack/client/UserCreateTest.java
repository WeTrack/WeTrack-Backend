package com.wetrack.client;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserCreateTest extends MessageResponseTest {

    @Test
    public void testUserCreateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_create/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);

        client.createUser(testUser, messageCallback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users"));
        assertThat(request.getBody().readUtf8(), is(gson.toJson(testUser)));

        client.createUser(testUser, callback(201));

        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users"));
        assertThat(request.getBody().readUtf8(), is(gson.toJson(testUser)));
    }

    @Test
    public void testUserCreateOnOkResponse() throws Exception {
        String testResponseBody = readResource("test_user_create/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_create/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);
        client.createUser(testUser, messageCallback(201));
        client.createUser(testUser, callback(201));

        assertThat(successful, is(true));
        assertThat(receivedStatusCode, is(201));
        assertThat(receivedEntity, notNullValue());
        assertThat(receivedException, nullValue());


        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedEntity.getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserCreateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_create/403.json");
        MockResponse testResponse = new MockResponse().setResponseCode(403).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_create/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);
        client.createUser(testUser, messageCallback(201));
        client.createUser(testUser, callback(201));

        assertThat(successful, is(false));
        assertThat(receivedStatusCode, is(403));
        assertThat(receivedErrorMessage, notNullValue());
        assertThat(receivedException, nullValue());


        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedErrorMessage.getMessage(), is(testResponseJson.optString("message")));
    }

}
