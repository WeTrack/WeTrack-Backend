package com.wetrack.service;

import com.wetrack.JerseyTest;
import com.wetrack.model.User;
import com.wetrack.util.CryptoUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class UserServiceTest extends JerseyTest {

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
        assertThat(postRequest.invoke().getStatus(), is(400)); // Bad Request as password is empty

        requestEntity.setPassword(password);
        postRequest = builder.buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        assertThat(postRequest.invoke().getStatus(), is(401)); // Unauthorized as the credential is incorrect

        requestEntity.setPassword(CryptoUtils.md5Digest(password));
        postRequest = builder.buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        assertThat(postRequest.invoke().getStatus(), is(200));
    }

    @Test
    public void testValidUserUpdateAndGet() throws JSONException {
        createUserWithAssertion();
        String token = loginUserWithAssertion();
        assertThat(token, notNullValue());

        Invocation getRequest = request("/users/" + username).buildGet();
        Response response = getRequest.invoke();
        assertThat(response.getStatus(), is(200));
        User userInResponse = parseStringAsUser(response.readEntity(String.class));

        userInResponse.setEmail(email);

        userInResponse.setBirthDate(null);
        // FIXME The Jackson serializer Jersey Test uses cannot correctly handle java.time classes,
        // FIXME it keeps on serializing them as Bean

        UserService.TokenUserRequest updateRequestEntity = new UserService.TokenUserRequest(token, userInResponse);
        Invocation updateRequest = request("/users/" + username, MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        assertThat(response.getStatus(), is(201));

        getRequest = request("/users/" + username).buildGet();
        response = getRequest.invoke();
        assertThat(response.getStatus(), is(200));
        userInResponse = parseStringAsUser(response.readEntity(String.class));

        assertThat(userInResponse.getEmail(), is(email));
    }

    /**
     * Invoking GET on a not-existed user.
     */
    @Test
    public void testGettingNotExistUser() {
        Invocation getRequest = target("/users/BlahBlahNotExist").request().buildGet();
        assertThat(getRequest.invoke().getStatus(), is(404));
    }

    /**
     * Invoking HEAD on an existed user.
     */
    @Test
    public void testHeadingExistUser() {
        createUserWithAssertion();
        Invocation headRequest = target("/users/" + username).request().build("HEAD");
        assertThat(headRequest.invoke().getStatus(), is(200));
    }

    @Test
    public void testUserPasswordUpdate() {
        createUserWithAssertion();
        String token = loginUserWithAssertion();

        Invocation validateRequest = target("/users/" + username + "/tokenValidate").request()
                .buildPost(Entity.entity(token, MediaType.APPLICATION_JSON_TYPE));
        Response response = validateRequest.invoke();
        assertThat(response.getStatus(), is(200));

        Invocation.Builder builder = target("/users/" + username + "/password").request();
        UserService.PasswordUpdateRequest updateRequestEntity = new UserService.PasswordUpdateRequest(token, null, null);
        Invocation updateRequest = builder.buildPost(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        assertThat(response.getStatus(), is(400)); // Bad Request as fields as empty

        updateRequestEntity.setOldPassword(password);

        updateRequest = builder.buildPost(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        assertThat(response.getStatus(), is(400)); // Bad Request as fields as empty

        updateRequestEntity.setNewPassword(anotherPassword);

        updateRequest = builder.buildPost(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        assertThat(response.getStatus(), is(401)); // Unauthorized as old password is incorrect

        updateRequestEntity.setOldPassword(CryptoUtils.md5Digest(password));

        updateRequest = builder.buildPost(Entity.entity(updateRequestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = updateRequest.invoke();
        assertThat(response.getStatus(), is(201)); // Unauthorized as old password is incorrect

        validateRequest = target("/users/" + username + "/tokenValidate").request()
                .buildPost(Entity.entity(token, MediaType.APPLICATION_JSON_TYPE));
        response = validateRequest.invoke();
        assertThat(response.getStatus(), is(401)); // Unauthorized as the token has been invalidated

        AuthenService.LoginRequest requestEntity =
                new AuthenService.LoginRequest(username, CryptoUtils.md5Digest(anotherPassword));
        Invocation loginRequest = target("/login").request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestEntity, MediaType.APPLICATION_JSON_TYPE));
        response = loginRequest.invoke();
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

        assertThat(response.getStatus(), is(400)); // Bad Request as username is empty

        newUser.setUsername(username);
        postRequest = builder.buildPost(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();

        assertThat(response.getStatus(), is(400)); // Bad Request as password is empty

        newUser.setPassword(password);
        postRequest = builder.buildPost(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));
        response = postRequest.invoke();

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

        String body = response.readEntity(String.class);
        JSONObject entity = null;
        try {
            entity = new JSONObject(body);
        } catch (JSONException ex) {
            fail("The response entity `" + body + "` is not in valid JSON format." );
        }
        return entity.getString("token");
    }

    private User parseStringAsUser(String userStr) {
        JSONObject userJson = null;
        try {
            userJson = new JSONObject(userStr);
        } catch (JSONException ex) {
            fail("The given string `" + userStr + "` is not in a valid JSON format.");
        }

        User user = new User();
        user.setUsername(userJson.optString("username"));
        user.setNickname(userJson.optString("nickname"));
        user.setEmail(userJson.optString("email"));
        user.setIconUrl(userJson.optString("iconUrl"));
        user.setBirthDate(LocalDate.parse(userJson.optString("birthDate")));

        return user;
    }

    /**
     * Create a user by invoking POST request and assert the creation is successful.
     */
    private void createUserWithAssertion() {
        User newUser = new User(username, password, nickname);
        Entity requestEntity = Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE);

        Invocation postRequest = target("/users").request(MediaType.APPLICATION_JSON).buildPost(requestEntity);
        Response response = postRequest.invoke();

        assertThat(response.getStatus(), is(201));
        assertThat(response.getHeaderString("Location"), endsWith("/users/" + username));
    }
}
