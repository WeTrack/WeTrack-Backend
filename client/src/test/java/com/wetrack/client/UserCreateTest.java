package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UserCreateTest extends WeTrackClientTest {

    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);
    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();

    @Test
    public void testUserCreateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_create/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);

        client.createUser(testUser, messageHelper.callback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users"));
        assertThat(request.getBody().readUtf8(), is(gson.toJson(testUser)));

        client.createUser(testUser, entityHelper.callback(201));

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
        client.createUser(testUser, messageHelper.callback(201));
        client.createUser(testUser, entityHelper.callback(201));

        entityHelper.assertReceivedEntity(201);
        messageHelper.assertReceivedMessage(true);

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedEntity().getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserCreateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_create/403.json");
        MockResponse testResponse = new MockResponse().setResponseCode(403).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_create/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);
        client.createUser(testUser, messageHelper.callback(201));
        client.createUser(testUser, entityHelper.callback(201));

        entityHelper.assertReceivedErrorMessage(403);
        messageHelper.assertReceivedMessage(false);

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedErrorMessage().getMessage(), is(testResponseJson.optString("message")));
    }

}
