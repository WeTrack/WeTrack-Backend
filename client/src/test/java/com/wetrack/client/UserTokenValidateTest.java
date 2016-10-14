package com.wetrack.client;

import com.wetrack.client.model.UserToken;
import com.wetrack.client.test.EntityResponseTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserTokenValidateTest extends EntityResponseTest<UserToken> {

    private String username = "robert-peng";
    private String token = "12345678absd";

    @Test
    public void testTokenValidateRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(200));
        client.tokenValidate(username, token, callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("POST"));
        assertThat(request.getPath(), is("/users/" + username + "/tokenValidate"));
        assertThat(request.getBody().readUtf8(), is(token));
    }

    @Test
    public void testTokenValidateOnErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(401));
        client.tokenValidate(username, token, callback(200));

        assertThat(receivedEntity, nullValue());
        assertThat(receivedException, nullValue());
        assertThat(receivedStatusCode, is(401));
    }

    @Test
    public void testTokenValidateOnOkResponse() throws Exception {
        String testResponse = readResource("test_token_validate/200.json");
        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

        client.userLogin(username, token, callback(200));

        assertThat(receivedEntity, notNullValue());
        assertThat(receivedStatusCode, is(200));
        assertThat(receivedException, nullValue());

        JSONObject responseBody = new JSONObject(testResponse);
        assertThat(receivedEntity.getToken(), is(responseBody.optString("token")));
        assertThat(receivedEntity.getUsername(), is(responseBody.optString("username")));
        assertThat(receivedEntity.getExpireTime().toString(), is(responseBody.optString("expireTime")));
    }

}
