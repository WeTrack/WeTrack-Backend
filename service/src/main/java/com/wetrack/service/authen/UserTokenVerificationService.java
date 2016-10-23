package com.wetrack.service.authen;

import com.google.gson.Gson;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class UserTokenVerificationService {
    private static final Logger LOG = LoggerFactory.getLogger(UserTokenVerificationService.class);

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;

    @POST
    @Path("/{username}/tokenVerify")
    public Response tokenVerify(@PathParam("username") @DefaultValue("") String username,
                                String token) {
        LOG.debug("POST /users/{}/tokenVerify", username);

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

        return ok(gson.toJson(userToken));
    }
}
