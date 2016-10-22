package com.wetrack.client;

import com.wetrack.client.model.Message;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.MessageResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import com.wetrack.util.CryptoUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserPasswordUpdateTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String token = "Not matter";
    private String oldPassword = "Not matter";
    private String newPassword = "Not matter";

    private MessageResponseTestHelper messageHelper = new MessageResponseTestHelper();
    private EntityResponseTestHelper<Message> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testUserPasswordUpdateRequestFormat() throws Exception {
        String testResponseBody = readResource("test_user_password_update/201.json");
        MockResponse testResponse = new MockResponse().setResponseCode(201).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        client.updateUserPassword(username, token, oldPassword, newPassword, messageHelper.callback(201));

        // Assert the request is sent as-is
        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("PUT"));
        assertThat(request.getPath(), is("/users/" + username + "/password"));

        UserService.PasswordUpdateRequest expectedRequestBody =
                new UserService.PasswordUpdateRequest(token, CryptoUtils.md5Digest(oldPassword), newPassword);
        assertThat(request.getBody().readUtf8(), is(gson.toJson(expectedRequestBody)));

        client.updateUserPassword(username, token, oldPassword, newPassword, entityHelper.callback(201));

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

        client.updateUserPassword(username, token, oldPassword, newPassword, messageHelper.callback(201));
        client.updateUserPassword(username, token, oldPassword, newPassword, entityHelper.callback(201));

        entityHelper.assertReceivedEntity(201);
        messageHelper.assertReceivedMessage(true);

        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedEntity().getMessage(), is(testResponseJson.optString("message")));
    }

    @Test
    public void testUserPasswordUpdateOnErrorResponse() throws Exception {
        String testResponseBody = readResource("test_user_password_update/401.json");
        MockResponse testResponse = new MockResponse().setResponseCode(401).setBody(testResponseBody);
        server.enqueue(testResponse);
        server.enqueue(testResponse);

        client.updateUserPassword(username, token, oldPassword, newPassword, entityHelper.callback(201));
        client.updateUserPassword(username, token, oldPassword, newPassword, messageHelper.callback(201));

        entityHelper.assertReceivedErrorMessage(401);
        messageHelper.assertReceivedMessage(false);


        JSONObject testResponseJson = new JSONObject(testResponseBody);
        assertThat(messageHelper.getReceivedMessage(), is(testResponseJson.optString("message")));
        assertThat(entityHelper.getReceivedErrorMessage().getMessage(), is(testResponseJson.optString("message")));
    }

}
