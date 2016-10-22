package com.wetrack.client;

import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTestHelper;
import com.wetrack.client.test.WeTrackClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserTokenValidateTest extends WeTrackClientTest {

    private String username = "robert-peng";
    private String token = "12345678absd";

    private EntityResponseTestHelper<UserToken> entityHelper = new EntityResponseTestHelper<>(gson);

    @Test
    public void testTokenValidateRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.tokenValidate(username, token, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users/" + username + "/tokenValidate"));
        assertThat(request.getBody().readUtf8(), is(token));
    }

    @Test
    public void testTokenValidateOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody(readResource("test_token_validate/401.json")));
        client.tokenValidate(username, token, entityHelper.callback(200));

        entityHelper.assertReceivedErrorMessage(401);
    }

    @Test
    public void testTokenValidateOnOkResponse() throws Exception {
        String testResponse = readResource("test_token_validate/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, token, entityHelper.callback(200));

        entityHelper.assertReceivedEntity(200);

        UserToken receivedToken = entityHelper.getReceivedEntity();
        JSONObject responseBody = new JSONObject(testResponse);
        assertThat(receivedToken.getToken(), is(responseBody.optString("token")));
        assertThat(receivedToken.getUsername(), is(responseBody.optString("username")));
        assertThat(receivedToken.getExpireTime().toString(), is(responseBody.optString("expireTime")));
    }

}
