package com.wetrack.service.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wetrack.dao.UserRepository;
import com.wetrack.model.User;
import com.wetrack.util.CryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserCreateService {
    private static final Logger LOG = LoggerFactory.getLogger(UserCreateService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;

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
}
