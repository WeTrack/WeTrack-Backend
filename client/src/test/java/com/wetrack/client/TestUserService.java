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

public class TestUserService {
    private static MockWebServer server;
    private static WeTrackClient client;

    private String username = "windy-chan";

    private int receivedStatusCode;
    private User receivedUser;
    private Throwable receivedError;

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new MockWebServer();
        server.start();

        client = new WeTrackClient(server.url("/").toString(), 3);
    }

    @Test
    public void testGetUserInfo() throws IOException, InterruptedException, URISyntaxException {
        server.enqueue(new MockResponse().setResponseCode(404));

        client.getUserInfo(username, userCallback());

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username));

        // Assert the error response is received and the subscriber is triggered
        assertThat(receivedStatusCode, is(404));
        assertThat(receivedUser, nullValue());

        // Test on valid JSON response
        String[] testResources = { "200", "200_empty_field", "200_missing_field", "200_null_field" };
        for (String testResource : testResources) {
            String fileName = "test_user_get/" + testResource + ".json";
            System.out.println("Test with " + fileName);
            URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            if (resourceUrl == null)
                fail("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
            String testResponse = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));

            server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));

            client.getUserInfo(username, userCallback());

            // Assert the response is received and the subscriber is triggered
            assertThat(receivedStatusCode, is(200));
            assertThat(receivedUser, notNullValue());

            JSONObject responseEntity = new JSONObject(testResponse);

            // Assert the content of the received entity
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

        // Test on invalid JSON response
        URL resourceUrl = getClass().getClassLoader().getResource("test_user_get/200_invalid_field.json");
        String testResponse = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));

        server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
        client.getUserInfo(username, userCallback());
        // Assert the error is received and the subscriber is triggered
        assertThat(receivedError, notNullValue());
        assertThat(receivedError instanceof JsonParseException, is(true));
        assertThat(receivedUser, nullValue());
        assertThat(receivedStatusCode, is(-1));
    }

    private AsyncCallback<User> userCallback() {
        return new AsyncCallback<User>() {
            @Override
            public void onReceive(User user) {
                receivedUser = user;
                receivedStatusCode = 200;
                receivedError = null;
            }

            @Override
            public void onErrorResponse(Response response) {
                receivedUser = null;
                receivedStatusCode = response.code();
                receivedError = null;
            }

            @Override
            public void onException(Throwable ex) {
                receivedUser = null;
                receivedStatusCode = -1;
                receivedError = ex;
            }
        };
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.shutdown();
    }
}
