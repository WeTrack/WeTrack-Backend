package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserUpdateTest extends WeTrackClientTest {

    private String token = "Not matter";

    private MessageResponseTestHelper messageHelper;
    private EntityResponseTestHelper<Message> entityHelper;

    @Before
    public void setUp() throws IOException {
        super.setUp();

        entityHelper = new EntityResponseTestHelper<>(gson);
        messageHelper = new MessageResponseTestHelper();
    }

    @Test
    public void testUserUpdateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);
        client.updateUser(testUser.getUsername(), token, testUser, messageHelper.callback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + testUser.getUsername()));

        UserService.TokenUserRequest expectedRequestBody =
                new UserService.TokenUserRequest(token, testUser);
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));

        client.updateUser(testUser.getUsername(), token, testUser, entityHelper.callback(201));

        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + testUser.getUsername()));
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));
    }

    @Test
    public void testUserUpdateOnOkResponse() throws Exception {
        String testResponseBody = readResource("test_user_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);

        client.updateUser(testUser.getUsername(), token, testUser, messageHelper.callback(201));
        client.updateUser(testUser.getUsername(), token, testUser, entityHelper.callback(201));

        entityHelper.assertReceivedEntity(201);
        messageHelper.assertReceivedMessage(true);

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedEntity().getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserUpdateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_update/404.json");
        MockResponse testResponse = new MockResponse().setResponseCode(404).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);

        client.updateUser(testUser.getUsername(), token, testUser, messageHelper.callback(201));
        client.updateUser(testUser.getUsername(), token, testUser, entityHelper.callback(201));

        entityHelper.assertReceivedErrorMessage(404);
        messageHelper.assertReceivedMessage(false);

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedErrorMessage().getMessage(), is(testResponseJson.optString("message")));
    }

}
