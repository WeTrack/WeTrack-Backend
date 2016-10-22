package com.wetrack.client;

import com.wetrack.client.config.Config;
import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import com.wetrack.util.CryptoUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserLoginTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String password = "Not matter";

    private EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testUserLoginRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(400));

        client.userLogin(username, password, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/login"));

        UserService.UserLoginRequest expectedRequestBody =
                new UserService.UserLoginRequest(username, CryptoUtils.md5Digest(password));
        assertThat(request.getBody().readUtf8(), is(Config.gson().toJson(expectedRequestBody)));
    }

    @Test
    public void testUserLoginOnOkResponse() throws Exception {
        String testResponse = readResource("test_user_login/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, password, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);

        UserToken receivedToken = entityHelper.getReceivedEntity();
        JSONObject responseBody = new JSONObject(testResponse);
        assertThat(receivedToken.getToken(), is(responseBody.optString("token")));
        assertThat(receivedToken.getUsername(), is(responseBody.optString("username")));
        assertThat(receivedToken.getExpireTime().toString(), is(responseBody.optString("expireTime")));
    }

    @Test
    public void testUserLoginOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody(readResource("test_user_login/401.json")));

        client.userLogin(username, password, entityHelper.callback(200));

        // Assert the error response is received and triggers the observer
        entityHelper.assertReceivedErrorMessage(401);
    }

}
