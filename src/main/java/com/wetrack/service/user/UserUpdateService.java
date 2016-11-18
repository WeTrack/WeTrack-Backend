package com.wetrack.service.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static com.wetrack.util.ResponseUtils.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserUpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(UserUpdateService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") @DefaultValue("") String username,
                               @QueryParam("token") @DefaultValue("") String token,
                               String requestBody) {
        LOG.debug("PUT  /users/{}/", username);

        if (username.isEmpty()) {
            LOG.debug("Received empty username. Returning `400 Bad Request`...");
            return badRequest("Given username cannot be empty.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOG.debug("Failed to find the user with username `{}`. Returning `404 Not Found`...", username);
            return notFound("User with given user name does not exist.");
        }

        if (token.trim().isEmpty()) {
            return badRequest("Token must be provided as query parameter.");
        }

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.debug("The given token is invalid or has expired. Returning `401 Unauthorized`...");
            return unauthorized("The given token is invalid or has expired, please log in again.");
        }

        if (!tokenInDB.getUsername().equals(username)) {
            LOG.debug("The given token does not belong the user trying to update. Returning `403 Forbidden`...");
            return unauthorized("You cannot update other's information.");
        }

        User updatedUser;
        try {
            updatedUser = gson.fromJson(requestBody, User.class);
        } catch (JsonSyntaxException ex) {
            LOG.debug("Received body is not in valid format, returning `400 Bad Request`...");
            return badRequest("The given request body is not in valid JSON format.");
        }

        updateUser(user, updatedUser);

        userRepository.update(user);
        LOG.debug("User updated successfully. Returning `200 OK`...");
        return okMessage("User updated.");
    }

    private void updateUser(User oldUser, User newUser) {
        if (newUser == null)
            return;

        if (newUser.getNickname() != null && !newUser.getNickname().trim().isEmpty())
            oldUser.setNickname(newUser.getNickname());

        if (newUser.getIconUrl() != null && !newUser.getIconUrl().trim().isEmpty())
            oldUser.setIconUrl(newUser.getIconUrl());

        if (newUser.getEmail() != null && !newUser.getEmail().trim().isEmpty())
            oldUser.setEmail(newUser.getEmail());

        if (newUser.getGender() != null)
            oldUser.setGender(newUser.getGender());

        if (newUser.getBirthDate() != null)
            oldUser.setBirthDate(newUser.getBirthDate());
    }
}
