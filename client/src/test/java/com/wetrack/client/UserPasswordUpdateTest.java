package com.wetrack.client;

import com.wetrack.client.model.User;
import com.wetrack.client.test.MessageResponseTest;
import com.wetrack.util.CryptoUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserPasswordUpdateTest extends MessageResponseTest {

    private String username = "robert-peng";
    private String token = "Not matter";
    private String oldPassword = "Not matter";
    private String newPassword = "Not matter";

    @Test
    public void testUserPasswordUpdateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_password_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        client.updateUserPassword(username, token, oldPassword, newPassword, messageCallback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + username + "/password"));

        UserService.PasswordUpdateRequest expectedRequestBody =
                new UserService.PasswordUpdateRequest(token, CryptoUtils.md5Digest(oldPassword), newPassword);
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));

        client.updateUserPassword(username, token, oldPassword, newPassword, callback(201));

        request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + username + "/password"));
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));
    }

    @Test
    public void testUserPasswordUpdateOnOkResponse() throws Exception {
        String testResponseBody = readResource("test_user_password_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        client.updateUserPassword(username, token, oldPassword, newPassword, messageCallback(201));
        client.updateUserPassword(username, token, oldPassword, newPassword, callback(201));

        assertThat(successful, is(true));
        assertThat(receivedStatusCode, is(201));
        assertThat(receivedEntity, notNullValue());
        assertThat(receivedException, nullValue());


        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedEntity.getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserPasswordUpdateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_password_update/401.json");
        MockResponse testResponse = new MockResponse().setResponseCode(401).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        client.updateUserPassword(username, token, oldPassword, newPassword, messageCallback(201));
        client.updateUserPassword(username, token, oldPassword, newPassword, callback(201));

        assertThat(successful, is(false));
        assertThat(receivedStatusCode, is(401));
        assertThat(receivedException, nullValue());
        assertThat(receivedErrorMessage, notNullValue());


        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(receivedMessage, is(testResponseJson.optString("message")));
        assertThat(receivedErrorMessage.getMessage(), is(testResponseJson.optString("message")));
    }

}
