package com.wetrack.service;

import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response userLogin(LoginRequest loginRequest) {
        LOG.debug("POST /login/");

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if (username == null || username.isEmpty()) {
            LOG.debug("The given username is empty. Returning `400 Bad Request`...");
            return badRequest("The given username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            LOG.debug("The given password is empty. Returning `400 Bad Request`...");
            return badRequest("The given password cannot be empty.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            LOG.debug("Login attempt with incorrect credential. Returning `401 Unauthorized`...");
            return unauthorized("The given username or password is incorrect.");
        }

        UserToken token = userTokenRepository.findByUsername(username);
        if (token == null) {
            token = new UserToken(username, 1, ChronoUnit.DAYS);
            userTokenRepository.insert(token);
        }

        JSONObject result = new JSONObject();
        result.put("token", token.getToken())
                .put("expireTime", token.getExpireTime());

        LOG.debug("User logged in successfully.");
        return ok(result);
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
