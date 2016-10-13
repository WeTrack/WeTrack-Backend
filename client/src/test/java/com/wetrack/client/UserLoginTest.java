package com.wetrack.client;

import com.wetrack.client.config.Config;
import com.wetrack.client.model.UserToken;
import com.wetrack.util.CryptoUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserLoginTest extends WeTrackClientTest<UserToken> {

    private String username = "robert-peng";
    private String password = "Not matter";

    @Test
    public void testUserLogin() throws Exception {
        String testResponse = readResource("test_user_login/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, password, callback(200));

        assertThat(receivedEntity, notNullValue());
        assertThat(receivedStatusCode, is(200));
        assertThat(receivedException, nullValue());

        JSONObject responseBody = new JSONObject(testResponse);
        assertThat(receivedEntity.getToken(), is(responseBody.optString("token")));
        assertThat(receivedEntity.getUsername(), is(responseBody.optString("username")));
        assertThat(receivedEntity.getExpireTime().toString(), is(responseBody.optString("expireTime")));
    }

    @Test
    public void testUserLoginOnErrorResponse() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(400));

        client.userLogin(username, password, callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/login"));
        assertThat(request.getBody().readUtf8(),
                is(Config.gson().toJson(new UserService.UserLoginRequest(username, CryptoUtils.md5Digest(password)))));

        // Assert the error response is received and triggers the observer
        assertThat(receivedException, nullValue());
        assertThat(receivedStatusCode, is(400));
        assertThat(receivedEntity, nullValue());

        server.enqueue(new MockResponse().setResponseCode(401));

        client.userLogin(username, password, callback(200));

        assertThat(receivedException, nullValue());
        assertThat(receivedStatusCode, is(401));
        assertThat(receivedEntity, nullValue());
    }

}
