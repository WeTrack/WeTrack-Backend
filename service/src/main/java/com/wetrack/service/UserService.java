package com.wetrack.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import com.wetrack.util.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users")
@Component
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;

    @HEAD
    @Path("/{username}")
    public Response userExists(@PathParam("username") @DefaultValue("") String username) {
        LOG.debug("HEAD /users/{}/", username);

        if (StringUtils.isBlank(username)) {
            LOG.debug("Received empty username. Returning `400 Bad Request`...");
            return badRequest("Given username cannot be empty.");
        }
        long matchCount = userRepository.countByUsername(username);
        if (matchCount == 0L) {
            LOG.debug("User with given username `{}` not found. Returning `404 Not Found`...", username);
            return notFound("User with given username not found.");
        }
        LOG.debug("User found. Returning `200 OK`...");
        return ok();
    }

    @GET
    @Path("/{username}")
    public Response getUserInfo(@PathParam("username") @DefaultValue("") String username) {
        LOG.debug("GET  /users/{}/", username);

        if (StringUtils.isBlank(username)) {
            LOG.debug("Received empty username. Returning `400 Bad Request`...");
            return badRequest("Given username cannot be empty.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOG.debug("User with given username not found. Returning `404 Not Found`...");
            return notFound("User with given username not found.");
        }

        return ok(gson.toJson(user));
    }

    @POST
    @Path("/{username}/tokenValidate")
    public Response tokenValidate(@PathParam("username") @DefaultValue("") String username,
                                  String token) {
        LOG.debug("POST /users/{}/tokenValidate", username);

        if (username.isEmpty()) {
            LOG.debug("The given username is empty. Returning `400 Bad Request`...");
            return badRequest("The given username cannot be empty.");
        }

        if (token == null || token.isEmpty()) {
            LOG.debug("The given token is empty. Returning `400 Bad Request`...");
            return badRequest("The given token cannot be empty.");
        }

        UserToken userToken = userTokenRepository.findByTokenStr(token);
        if (userToken == null || !userToken.getUsername().equals(username)
                || userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.debug("The given token is invalid or has expired. Returning `401 Unauthorized`...");
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        }

        return ok();
    }

    @POST
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") @DefaultValue("") String username,
                               String requestBody) {
        LOG.debug("PUT  /users/{}/", username);

        if (username.isEmpty()) {
            LOG.debug("Received empty username. Returning `400 Bad Request`...");
            return badRequest("Given username cannot be empty.");
        }

        TokenUserRequest tokenUserRequest;
        try {
            tokenUserRequest = gson.fromJson(requestBody, TokenUserRequest.class);
        } catch (JsonSyntaxException ex) {
            LOG.debug("Received body is not in valid format, returning `400 Bad Request`...");
            return badRequest("The given request body is not in valid JSON format.");
        }

        String tokenId = tokenUserRequest.getToken();
        if (tokenUserRequest.getToken().isEmpty()) {
            LOG.debug("Received empty token. Returning `400 Bad Request...`");
            return badRequest("Given token cannot be empty.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOG.debug("Failed to find the user with username `{}`. Returning `404 Not Found`...", username);
            return notFound("User with given user name does not exist.");
        }

        UserToken token = userTokenRepository.findByTokenStr(tokenId);
        if (token == null || token.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.debug("The given token is invalid or has expired. Returning `401 Unauthorized`...");
            return unauthorized("The given token is invalid or has expired, please log in again.");
        }

        if (!token.getUsername().equals(username)) {
            LOG.debug("The given token does not belong the user trying to update. Returning `403 Forbidden`...");
            return forbidden("You cannot update others' information.");
        }

        User userInRequest = tokenUserRequest.getUser();
        updateUser(user, userInRequest);

        userRepository.update(user);
        LOG.debug("User updated successfully. Returning `201 Created`...");
        return created("/users/" + username, "User updated.");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String requestBody) {
        LOG.debug("POST /users/");

        User user;
        try {
            user = gson.fromJson(requestBody, User.class);
        } catch (JsonSyntaxException ex) {
            LOG.debug("Received body in invalid format. Returning `400 Bad Request`...");
            return badRequest("The given request body is not in valid JSON format.");
        }

        String username = user.getUsername();

        if (username == null || username.isEmpty()) {
            LOG.debug("The given username is empty. Returning `400 Bad Request`...");
            return badRequest("Given user info must contain username");
        }
        if (userRepository.countByUsername(username) > 0) {
            LOG.debug("User with given username already exists. Returning `403 Forbidden`...");
            return forbidden("User with same username already exists.");
        }

        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            LOG.debug("The given password is empty. Returning `400 Bad Request`...");
            return badRequest("Given user info must contain password");
        }
        user.setPassword(CryptoUtils.md5Digest(password));

        if (user.getNickname() == null || user.getNickname().isEmpty())
            user.setNickname(user.getUsername());

        if (user.getBirthDate() == null)
            user.setBirthDate(LocalDate.ofEpochDay(0));

        userRepository.insert(user);
        LOG.debug("User created successfully. Returning `201 Created`...");
        return created("/users/" + username, String.format("User `%s` created.", username));
    }

    @POST
    @Path("/{username}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserPassword(@PathParam("username") @DefaultValue("") String username,
                                       String requestBody) {
        LOG.debug("POST /users/{}/password", username);

        PasswordUpdateRequest updateRequest;
        try {
            updateRequest = gson.fromJson(requestBody, PasswordUpdateRequest.class);
        } catch (JsonSyntaxException ex) {
            LOG.debug("The received body is not in valid format. Returning `400 Bad Request`...");
            return badRequest("The given request body is not in valid JSON format.");
        }

        if (username.isEmpty()) {
            LOG.debug("The given username is empty. Returning `400 Bad Request`...");
            return badRequest("The given username cannot be empty.");
        }

        if (updateRequest == null) {
            LOG.debug("POST with empty body. Returning `400 Bad Request`...");
            return badRequest("The given request body cannot be empty.");
        }

        String token = updateRequest.getToken();
        if (token == null || token.trim().isEmpty()) {
            LOG.debug("The given token is empty. Returning `400 Bad Request`...");
            return badRequest("A token must be provided to update the given user.");
        }

        String oldPassword = updateRequest.getOldPassword();
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            LOG.debug("The given old password is empty. Returning `400 Bad Request`...");
            return badRequest("The original password must be provided to change the password.");
        }

        String newPassword = updateRequest.getNewPassword();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            LOG.debug("The given new password is empty. Returning `400 Bad Request`...");
            return badRequest("The new password cannot be empty.");
        }

        UserToken userToken = userTokenRepository.findByTokenStr(token);
        if (userToken == null) {
            LOG.debug("The given token is invalid or has expired. Returning `401 Unauthorized`...");
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        }

        if (!userToken.getUsername().equals(username)) {
            LOG.debug("User trying to change other's password. Returning `403 Forbidden`...");
            return forbidden("You cannot change other's password.");
        }

        User userInDb = userRepository.findByUsername(username);
        if (userInDb == null) {
            LOG.debug("User with given username does not exist. Returning `404 Not Found`...");
            return notFound("User with given username does not exist.");
        }

        if (!userInDb.getPassword().equals(oldPassword)) {
            LOG.debug("The given old password is incorrect. Returning `401 Unauthorized`...");
            return unauthorized("The given old password is incorrect.");
        }

        userInDb.setPassword(CryptoUtils.md5Digest(newPassword));
        userRepository.update(userInDb);

        userTokenRepository.delete(userToken);
        return created("/users/" + username, "User password successfully updated.");
    }

    private void updateUser(User oldUser, User newUser) {
        if (newUser.getUsername() != null && !newUser.getUsername().trim().isEmpty())
            oldUser.setNickname(newUser.getUsername());

        if (newUser.getIconUrl() != null && !newUser.getIconUrl().trim().isEmpty())
            oldUser.setIconUrl(newUser.getIconUrl());

        if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty())
            oldUser.setEmail(newUser.getEmail());

        if (newUser.getGender() != null)
            oldUser.setGender(newUser.getGender());

        if (newUser.getBirthDate() != null)
            oldUser.setBirthDate(newUser.getBirthDate());
    }

    static class PasswordUpdateRequest {
        private String token;
        private String oldPassword;
        private String newPassword;

        PasswordUpdateRequest() {}

        PasswordUpdateRequest(String token, String oldPassword, String newPassword) {
            this.token = token;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        String getToken() {
            return token;
        }
        void setToken(String token) {
            this.token = token;
        }
        String getOldPassword() {
            return oldPassword;
        }
        void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
        String getNewPassword() {
            return newPassword;
        }
        void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    static class TokenUserRequest {
        private String token;
        private User user;

        TokenUserRequest() {}

        TokenUserRequest(String token, User user) {
            this.token = token;
            this.user = user;
        }

        String getToken() {
            return token;
        }
        void setToken(String token) {
            this.token = token;
        }
        User getUser() {
            return user;
        }
        void setUser(User user) {
            this.user = user;
        }
    }
}
