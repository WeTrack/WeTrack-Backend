package com.wetrack.client;

import com.google.gson.JsonParseException;
import com.wetrack.client.model.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UserGetTest extends WeTrackClientTest<User> {

    private String username = "windy-chan";

    @Test
    public void testGetUserInfo() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        client.getUserInfo(username, callback(200));

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username));

        // Assert the error response is received and the subscriber is triggered
        assertThat(receivedStatusCode, is(404));
        assertThat(receivedEntity, nullValue());

        // Test on valid JSON response
        String[] testResources = { "200", "200_empty_field", "200_missing_field", "200_null_field" };
        for (String testResource : testResources) {
            String fileName = "test_user_get/" + testResource + ".json";
            System.out.println("Test with " + fileName);

            String testResponse = readResource(fileName);

            server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

            client.getUserInfo(username, callback(200));

            // Assert the response is received and the subscriber is triggered
            assertThat(receivedStatusCode, is(200));
            assertThat(receivedEntity, notNullValue());

            JSONObject responseEntity = new JSONObject(testResponse);

            // Assert the content of the received entity
            assertThat(receivedEntity.getUsername(), is(username));
            String email = responseEntity.optString("email");
            if (email == null || email.trim().isEmpty())
                assertThat(receivedEntity.getEmail() == null || receivedEntity.getEmail().trim().isEmpty(), is(true));
            else
                assertThat(receivedEntity.getEmail(), is(email));

            String iconUrl = responseEntity.optString("iconUrl");
            if (iconUrl == null || iconUrl.trim().isEmpty())
                assertThat(receivedEntity.getIconUrl() == null || receivedEntity.getIconUrl().trim().isEmpty(), is(true));
            else
                assertThat(receivedEntity.getIconUrl(), is(iconUrl));

            String nickname = responseEntity.optString("nickname");
            if (nickname == null || nickname.trim().isEmpty())
                assertThat(receivedEntity.getNickname() == null || receivedEntity.getNickname().trim().isEmpty(), is(true));
            else
                assertThat(receivedEntity.getNickname(), is(nickname));

            User.Gender gender;
            try {
                gender = User.Gender.valueOf(responseEntity.optString("gender"));
                assertThat(receivedEntity.getGender(), is(gender));
            } catch (Throwable ex) {
                assertThat(receivedEntity.getGender(), nullValue());
            }

            String birthDateStr = responseEntity.optString("birthDate");
            if (birthDateStr == null || birthDateStr.trim().isEmpty())
                assertThat(receivedEntity.getBirthDate(), nullValue());
            else
                assertThat(receivedEntity.getBirthDate(), is(LocalDate.parse(birthDateStr)));
        }

        // Test on invalid JSON response
        URL resourceUrl = getClass().getClassLoader().getResource("test_user_get/200_invalid_field.json");
        String testResponse = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));

        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserInfo(username, callback(200));
        // Assert the error is received and the subscriber is triggered
        assertThat(receivedException, notNullValue());
        assertThat(receivedException instanceof JsonParseException, is(true));
        assertThat(receivedEntity, nullValue());
        assertThat(receivedStatusCode, is(-1));
    }
}
