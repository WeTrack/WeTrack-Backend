package com.wetrack.service;

import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;

    @HEAD
    @Path("/{username}")
    public Response userExists(@PathParam("username") @DefaultValue("") String username) {
        if (StringUtils.isBlank(username))
            return badRequest("Given username cannot be empty.");
        long matchCount = userRepository.countByUsername(username);
        if (matchCount == 0L)
            return notFound("User with given username not found.");
        return ok();
    }

    @GET
    @Path("/{username}")
    public Response getUserInfo(@PathParam("username") @DefaultValue("") String username) {
        if (StringUtils.isBlank(username))
            return badRequest("Given username cannot be empty.");
        User user = userRepository.findByUsername(username);
        if (user == null)
            return notFound("User with given username not found.");

        JSONObject result = new JSONObject();
        result.append("username", user.getUsername()).append("nickname", user.getNickname())
                .append("iconUrl", user.getIconUrl()).append("email", user.getEmail())
                .append("gender", user.getGender().name()).append("birthDate", user.getBirthDate().format(formatter));
        return ok(result);
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") @DefaultValue("") String username,
                               TokenUserRequest request) {
        if (username.isEmpty())
            return badRequest("Given username cannot be empty.");
        String tokenId = request.getToken();
        if (request.getToken().isEmpty())
            return badRequest("Given token cannot be empty.");

        User user = userRepository.findByUsername(username);
        if (user == null)
            return notFound("User with given user name does not exist.");
        UserToken token = userTokenRepository.findByTokenStr(tokenId);
        if (token == null || token.getExpireTime().isBefore(LocalDateTime.now()))
            return forbidden("The given token is expired, please log in again.");
        if (!token.getUsername().equals(username))
            return forbidden("You cannot update others' information.");

        User userInRequest = request.getUser();
        updateUser(user, userInRequest);

        userRepository.update(user);
        return created("/users/" + username, "User updated.");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User userInRequest) {
        String username = userInRequest.getUsername();

        if (username.isEmpty())
            return badRequest("Given user info must contain username");
        if (userRepository.countByUsername(username) > 0)
            return forbidden("User with same username already exists.");

        String password = userInRequest.getPassword();
        if (password.isEmpty())
            return badRequest("Given user info must contain password");
        userInRequest.setPassword(new Md5Hash(password).toHex());

        userRepository.insert(userInRequest);
        return created("/users/" + username, String.format("User `%s` created.", username));
    }

    private void updateUser(User oldUser, User newUser) {
        if (!newUser.getUsername().trim().isEmpty())
            oldUser.setNickname(newUser.getUsername());

        if (!newUser.getIconUrl().trim().isEmpty())
            oldUser.setIconUrl(newUser.getIconUrl());

        if (!newUser.getEmail().trim().isEmpty())
            oldUser.setEmail(newUser.getEmail());

        oldUser.setGender(newUser.getGender());

        oldUser.setBirthDate(newUser.getBirthDate());
    }

    public static class TokenUserRequest {
        private String token;
        private User user;

        public TokenUserRequest() {}

        public TokenUserRequest(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }
}
