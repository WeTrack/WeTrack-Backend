package com.wetrack.client;

import com.google.gson.JsonParseException;
import com.wetrack.client.model.User;
import com.wetrack.client.test.EntityResponseHelper;
import com.wetrack.client.test.WeTrackClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserGetTest extends WeTrackClientTest {

    private String username = "windy-chan";

    private EntityResponseHelper<User> entityHelper = new EntityResponseHelper<>(gson);

    @Test
    public void testUserGetRequestFormat() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));

        client.getUserInfo(username, entityHelper.callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username));
    }

    @Test
    public void testUserGetOnErrorResponse() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(readResource("test_user_get/404.json")));

        client.getUserInfo(username, entityHelper.callback(200));

        // Assert the error response is received and the subscriber is triggered
        entityHelper.assertReceivedErrorMessage(404);
    }

    @Test
    public void testUserGetOnValidResponse() throws Exception {
        String[] testResources = { "200", "200_empty_field", "200_missing_field", "200_null_field" };

        for (String testResource : testResources) {
            String fileName = "test_user_get/" + testResource + ".json";
            System.out.println("Test with " + fileName);

            String testResponse = readResource(fileName);

            server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

            client.getUserInfo(username, entityHelper.callback(200));

            // Assert the response is received and the subscriber is triggered
            entityHelper.assertReceivedEntity(200);

            JSONObject responseEntity = new JSONObject(testResponse);

            // Assert the content of the received entity
            User receivedUser = entityHelper.getReceivedEntity();
            assertThat(receivedUser.getUsername(), is(username));
            String email = responseEntity.optString("email");
            if (email == null || email.trim().isEmpty())
                assertThat(receivedUser.getEmail() == null || receivedUser.getEmail().trim().isEmpty(), is(true));
            else
                assertThat(receivedUser.getEmail(), is(email));

            String iconUrl = responseEntity.optString("iconUrl");
            if (iconUrl == null || iconUrl.trim().isEmpty())
                assertThat(receivedUser.getIconUrl() == null || receivedUser.getIconUrl().trim().isEmpty(), is(true));
            else
                assertThat(receivedUser.getIconUrl(), is(iconUrl));

            String nickname = responseEntity.optString("nickname");
            if (nickname == null || nickname.trim().isEmpty())
                assertThat(receivedUser.getNickname() == null || receivedUser.getNickname().trim().isEmpty(), is(true));
            else
                assertThat(receivedUser.getNickname(), is(nickname));

            User.Gender gender;
            try {
                gender = User.Gender.valueOf(responseEntity.optString("gender"));
                assertThat(receivedUser.getGender(), is(gender));
            } catch (Throwable ex) {
                assertThat(receivedUser.getGender(), nullValue());
            }

            String birthDateStr = responseEntity.optString("birthDate");
            if (birthDateStr == null || birthDateStr.trim().isEmpty())
                assertThat(receivedUser.getBirthDate(), nullValue());
            else
                assertThat(receivedUser.getBirthDate(), is(LocalDate.parse(birthDateStr)));
        }
    }

    @Test
    public void testUserGetOnInvalidResponse() throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource("test_user_get/200_invalid_field.json");
        String testResponse = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));

        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserInfo(username, entityHelper.callback(200));
        // Assert the error is received and the subscriber is triggered
        assertThat(entityHelper.getReceivedException(), notNullValue());
        assertThat(entityHelper.getReceivedException() instanceof JsonParseException, is(true));
        assertThat(entityHelper.getReceivedEntity(), nullValue());
    }
}
