package com.wetrack.service.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.util.CryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserPasswordUpdateService {
    private static final Logger LOG = LoggerFactory.getLogger(UserPasswordUpdateService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;

    @PUT
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

        if (updateRequest.oldPassword == null || updateRequest.oldPassword.trim().isEmpty()) {
            LOG.debug("The given old password is empty. Returning `400 Bad Request`...");
            return badRequest("The original password must be provided to change the password.");
        }

        if (updateRequest.newPassword == null || updateRequest.newPassword.trim().isEmpty()) {
            LOG.debug("The given new password is empty. Returning `400 Bad Request`...");
            return badRequest("The new password cannot be empty.");
        }

        User userInDb = userRepository.findByUsername(username);
        if (userInDb == null) {
            LOG.debug("User with given username does not exist. Returning `404 Not Found`...");
            return notFound("User with given username does not exist.");
        }

        if (!userInDb.getPassword().equals(updateRequest.oldPassword)) {
            LOG.debug("The given old password is incorrect. Returning `401 Unauthorized`...");
            return unauthorized("The given old password is incorrect.");
        }

        userInDb.setPassword(CryptoUtils.md5Digest(updateRequest.newPassword));
        userRepository.update(userInDb);

        userTokenRepository.deleteByUsername(username);
        return okMessage("User password successfully updated.");
    }

    private static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;
    }
}
