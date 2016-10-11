package com.wetrack.client;

import com.google.gson.JsonParseException;
import com.wetrack.client.config.Config;
import com.wetrack.client.model.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import retrofit2.Retrofit;
import rx.Subscriber;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestUserService {

    private static MockWebServer server;
    private static UserService userService;

    private String username = "windy-chan";

    private Throwable receivedError;
    private User receivedUser;

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new MockWebServer();
        server.start();

        Retrofit retrofit = Config.retrofit(server.url("/").toString(), 0);
        userService = retrofit.create(UserService.class);
    }

    @Test
    public void testGetUserInfo() throws IOException, InterruptedException, URISyntaxException {
        server.enqueue(new MockResponse().setResponseCode(404));
        userService.getUserInfo(username).subscribe(userSubscriber());

        RecordedRequest request = server.takeRequest(3, TimeUnit.SECONDS);

        // Assert the request is sent as-is
        assertThat(request, notNullValue());
        assertThat(request.getMethod(), is("GET"));
        assertThat(request.getPath(), is("/users/" + username));

        // Assert the error is received and the subscriber is triggered
        assertThat(receivedError, notNullValue());
        assertThat(receivedUser, nullValue());

        // Test on valid JSON response
        String[] testResouces = {"200", "200_empty_field", "200_missing_field", "200_null_field" };
        for (String testResource : testResouces) {
            String fileName = "test_user_get/" + testResource + ".json";
            System.out.println("Test with " + fileName);
            URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            if (resourceUrl == null)
                fail("Failed to find `" + fileName + "` in resources folder. Check if it is deleted.");
            String testResponse = new String(Files.readAllBytes(Paths.get(resourceUrl.toURI())));

            server.enqueue(new MockResponse().setResponseCode(200).setBody(testResponse));
            userService.getUserInfo(username).subscribe(userSubscriber());

            // Assert the response is received and the subscriber is triggered
            if (receivedError != null) {
                receivedError.printStackTrace();
                fail("`receivedError` is not null. Assertion failed.");
            }
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
        userService.getUserInfo(username).subscribe(userSubscriber());
        // Assert the error is received and the subscriber is triggered
        assertThat(receivedError, notNullValue());
        assertThat(receivedUser, nullValue());
        assertThat(receivedError instanceof JsonParseException, is(true));
    }

    private Subscriber<User> userSubscriber() {
        return new Subscriber<User>() {
            @Override public void onCompleted() {}

            @Override public void onError(Throwable e) {
                System.out.println("Received error: " + e.getMessage());
                receivedUser = null;
                receivedError = e;
            }

            @Override public void onNext(User user) {
                System.out.println("Received user: " + user);
                receivedUser = user;
                receivedError = null;
            }
        };
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.shutdown();
    }
}
