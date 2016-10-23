package com.wetrack.service;

import com.google.gson.Gson;
import com.wetrack.JerseyTest;
import com.wetrack.config.SpringConfig;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import com.wetrack.util.CryptoUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class UserServiceTest extends JerseyTest {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceTest.class);

    private Gson gson = new SpringConfig().gson();

    private String username = "robert-peng";
    private String nickname = "Robert Peng";
    private String password = "I\'m a password";
    private String anotherPassword = "I\'m another password";
    private String email = "robert.peng@example.com";

    @Test
    public void testLoginWithInvalidCredential() {
        createUserWithAssertion();

        AuthenService.LoginRequest requestEntity = new AuthenService.LoginRequest();
        requestEntity.setUsername(username);

        Invocation.Builder builder = target("/login").request();
        Invocation postRequest = builder.buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        Response response = postRequest.invoke();
        logResponse(response, "user login with empty password");
        assertThat(response.getStatus(), is(400)); // Bad Request as password is empty

        requestEntity.setPassword(password);
        postRequest = builder.buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();
        logResponse(response, "user login with plain password");
        assertThat(response.getStatus(), is(401)); // Unauthorized as the credential is incorrect

        requestEntity.setPassword(CryptoUtils.md5Digest(password));
        postRequest = builder.buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();
        logResponse(response, "user login");
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void testValidUserUpdateAndGet() {
        createUserWithAssertion();
        String token = loginUserWithAssertion();
        assertThat(token, notNullValue());

        Invocation getRequest = request("/users/" + username).buildGet();
        Response response = getRequest.invoke();
        assertThat(response.getStatus(), is(200));
        String responseBody = response.readEntity(String.class);
        LOG.debug("Received response body on user query:\n==========================\n{}\n==========================",
                responseBody);
        User userInResponse = gson.fromJson(responseBody, User.class);
        assertThat(userInResponse.getPassword().isEmpty(), is(true));

        userInResponse.setEmail(email);

        userInResponse.setBirthDate(null);
        // FIXME The Jackson serializer Jersey Test uses cannot correctly handle java.time classes,
        // FIXME it keeps on serializing them as Bean

        UserService.TokenUserRequest updateRequestEntity = new UserService.TokenUserRequest(token, userInResponse);
        Invocation updateRequest = request("/users/" + username, MediaType.APPLICATION_JSON_TYPE)
                .buildPut(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        logResponse(response, "user update");
        assertThat(response.getStatus(), is(201));

        getRequest = request("/users/" + username).buildGet();
        response = getRequest.invoke();
        assertThat(response.getStatus(), is(200));
        responseBody = response.readEntity(String.class);
        LOG.debug("Received response body on user query:\n==========================\n{}\n==========================",
                responseBody);
        userInResponse = gson.fromJson(responseBody, User.class);

        assertThat(userInResponse.getEmail(), is(email));
    }

    /**
     * Invoking GET on a not-existed user.
     */
    @Test
    public void testGettingNotExistUser() {
        Invocation getRequest = target("/users/BlahBlahNotExist").request().buildGet();
        Response response = getRequest.invoke();
        logResponse(response, "query of not exist user");
        assertThat(response.getStatus(), is(404));
    }

    /**
     * Invoking HEAD on an existed user.
     */
    @Test
    public void testHeadingExistUser() {
        createUserWithAssertion();
        Invocation headRequest = target("/users/" + username).request().build("HEAD");
        Response response = headRequest.invoke();
        logResponse(response, "user existence detect");
        assertThat(headRequest.invoke().getStatus(), is(200));
    }

    @Test
    public void testUserPasswordUpdate() {
        createUserWithAssertion();
        String token = loginUserWithAssertion();

        Invocation validateRequest = target("/users/" + username + "/tokenValidate").request()
                .buildPost(Entity.entity(token, MediaType.APPLICATION_JSON_TYPE));
        Response response = validateRequest.invoke();
        logResponse(response, "user token validate");
        assertThat(response.getStatus(), is(200));

        Invocation.Builder builder = target("/users/" + username + "/password").request();
        UserService.PasswordUpdateRequest updateRequestEntity = new UserService.PasswordUpdateRequest(null, null);
        Invocation updateRequest = builder.buildPut(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        logResponse(response, "user password update with empty fields");
        assertThat(response.getStatus(), is(400)); // Bad Request as fields as empty

        updateRequestEntity.setOldPassword(password);

        updateRequest = builder.buildPut(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        logResponse(response, "user password update with empty new password");
        assertThat(response.getStatus(), is(400)); // Bad Request as fields as empty

        updateRequestEntity.setNewPassword(anotherPassword);

        updateRequest = builder.buildPut(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        logResponse(response, "user password update with incorrect old password");
        assertThat(response.getStatus(), is(401)); // Unauthorized as old password is incorrect

        updateRequestEntity.setOldPassword(CryptoUtils.md5Digest(password));

        updateRequest = builder.buildPut(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        logResponse(response, "user password update");
        assertThat(response.getStatus(), is(200));

        validateRequest = target("/users/" + username + "/tokenValidate").request()
                .buildPost(Entity.entity(token, MediaType.APPLICATION_JSON_TYPE));
        response = validateRequest.invoke();
        logResponse(response, "expired user token validate");
        assertThat(response.getStatus(), is(401)); // Unauthorized as the token has been invalidated

        AuthenService.LoginRequest requestEntity =
                new AuthenService.LoginRequest(username, CryptoUtils.md5Digest(anotherPassword));
        Invocation loginRequest = target("/login").request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = loginRequest.invoke();
        logResponse(response, "user login");
        assertThat(response.getStatus(), is(200));
    }

    /**
     * Try to create a user with occupied username.
     */
    @Test
    public void testCreatingDuplicateUser() {
        createUserWithAssertion();

        User newUser = new User(username, nickname, password);
        newUser.setEmail(email);
        Entity requestEntity = Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE);

        Invocation postRequest = target("/users").request(MediaType.APPLICATION_JSON).buildPost(requestEntity);
        Response response = postRequest.invoke();
        logResponse(response, "duplicate user create");

        assertThat(response.getStatus(), is(403)); // Forbidden
    }

    /**
     * Try to create a user without providing certain field.
     */
    @Test
    public void testCreatingUserWithEmptyField() {
        Invocation.Builder builder = target("/users").request(MediaType.APPLICATION_JSON);

        User newUser = new User();
        Invocation postRequest = builder.buildPost(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
        Response response = postRequest.invoke();
        logResponse(response, "user create with empty username");

        assertThat(response.getStatus(), is(400)); // Bad Request as username is empty

        newUser.setUsername(username);
        postRequest = builder.buildPost(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();
        logResponse(response, "user create with empty password");

        assertThat(response.getStatus(), is(400)); // Bad Request as password is empty

        newUser.setPassword(password);
        postRequest = builder.buildPost(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();
        logResponse(response, "user create");

        assertThat(response.getStatus(), is(201));
    }

    private String loginUserWithAssertion() {
        AuthenService.LoginRequest requestEntity = new AuthenService.LoginRequest();
        requestEntity.setUsername(username);
        requestEntity.setPassword(CryptoUtils.md5Digest(password));

        Invocation loginRequest = target("/login").request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));

        Response response = loginRequest.invoke();
        assertThat(response.getStatus(), is(200));

        String responseBody = response.readEntity(String.class);
        LOG.debug("Received response body on user login:\n==========================\n{}\n==========================",
                responseBody);
        UserToken token = gson.fromJson(responseBody, UserToken.class);
        return token.getToken();
    }

    /**
     * Create a user by invoking POST request and assert the creation is successful.
     */
    private void createUserWithAssertion() {
        User newUser = new User(username, password, nickname);
        Entity requestEntity = Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE);

        Invocation postRequest = target("/users").request(MediaType.APPLICATION_JSON).buildPost(requestEntity);
        Response response = postRequest.invoke();
        logResponse(response, "user create");

        assertThat(response.getStatus(), is(201));
        assertThat(response.getHeaderString("Location"), endsWith("/users/" + username));
    }

    private void logResponse(Response response, String event) {
        if (LOG.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(String.valueOf(response.getStatus()));
            for (Map.Entry<String, List<String>> entry : response.getStringHeaders().entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue())
                    builder.append('\n').append(key).append(": ").append(value);
            }
            builder.append("\n\n").append(response.readEntity(String.class));
            LOG.debug("Received response on {}:\n==========================\n{}\n==========================",
                    event, builder.toString());
        }
    }
}
