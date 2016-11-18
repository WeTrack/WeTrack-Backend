package com.wetrack.service.user;

import com.google.gson.Gson;
import com.wetrack.dao.UserRepository;
import com.wetrack.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.wetrack.util.ResponseUtils.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserGetService {
    private static final Logger LOG = LoggerFactory.getLogger(UserGetService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;

    @HEAD
    @Path("/{username}")
    public Response userExists(@PathParam("username") @DefaultValue("") String username) {
        LOG.debug("HEAD /users/{}/", username);

        long matchCount = userRepository.countByUsername(username);
        if (matchCount == 0L) {
            LOG.debug("User with given username `{}` not found. Returning `404 Not Found`...", username);
            return notFound();
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

        user.setPassword(null);  // Set the password field to `null` to avoid serialization
        return ok(gson.toJson(user));
    }

}
