package com.wetrack.client;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTest;
import com.wetrack.util.CryptoUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserUpdateTest extends MessageResponseTest {

    private String token = "Not matter";

    @Test
    public void testUserUpdateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);
        client.updateUser(testUser.getUsername(), token, testUser, messageCallback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + testUser.getUsername()));

        UserService.TokenUserRequest expectedRequestBody =
                new UserService.TokenUserRequest(token, testUser);
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));

        client.updateUser(testUser.getUsername(), token, testUser, callback(201));

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

        client.updateUser(testUser.getUsername(), token, testUser, messageCallback(201));
        client.updateUser(testUser.getUsername(), token, testUser, callback(201));

        assertThat(successful, is(true));
        assertThat(receivedStatusCode, is(201));
        assertThat(receivedException, nullValue());

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedEntity.getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserUpdateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_update/404.json");
        MockResponse testResponse = new MockResponse().setResponseCode(404).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        String testUserStr = readResource("test_user_update/example_user.json");
        User testUser = gson.fromJson(testUserStr, User.class);

        client.updateUser(testUser.getUsername(), token, testUser, messageCallback(201));
        client.updateUser(testUser.getUsername(), token, testUser, callback(201));

        assertThat(successful, is(false));
        assertThat(receivedStatusCode, is(404));
        assertThat(receivedException, nullValue());

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedErrorMessage.getMessage(), is(testResponseJson.optString("message")));
    }

}
