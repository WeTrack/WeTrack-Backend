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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updateUser(@PathParam("username") @DefaultValue("") String username,
                               @FormParam("token") @DefaultValue("") String tokenId,
                               @FormParam("user") @DefaultValue("") String userJson) {
        if (username.isEmpty())
            return badRequest("Given username cannot be empty.");
        if (tokenId.isEmpty())
            return badRequest("Given token cannot be empty.");

        User user = userRepository.findByUsername(username);
        if (user == null)
            return notFound("User with given user name does not exist.");
        UserToken token = userTokenRepository.findByTokenStr(tokenId);
        if (token == null || token.getExpireTime().isBefore(LocalDateTime.now()))
            return forbidden("The given token is expired, please log in again.");
        if (!token.getUsername().equals(username))
            return forbidden("You cannot update others' information.");

        try {
            JSONObject userInfo = new JSONObject(userJson);
            setUser(user, userInfo);
        } catch (JSONException ex) {
            return badRequest("The given user information is not in valid JSON format.");
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }

        userRepository.update(user);
        return created("/users/" + username, "User updated.");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createUser(@FormParam("user") @DefaultValue("") String userJson) {
        if (StringUtils.isBlank(userJson))
            return badRequest("Given new user info cannot be empty.");
        JSONObject userInfo;
        try {
            userInfo = new JSONObject(userJson);
        } catch (JSONException ex) {
            return badRequest("Given user info is not in valid JSON format.");
        }

        String username = userInfo.optString("username");
        if (username.isEmpty())
            return badRequest("Given user info must contain username");
        if (userRepository.countByUsername(username) > 0)
            return forbidden("User with same username already exists.");

        String password = userInfo.optString("password");
        if (password.isEmpty())
            return badRequest("Given user info must contain password");
        password = new Md5Hash(password).toHex();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        try {
            setUser(user, userInfo);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }

        userRepository.insert(user);
        return created("/users/" + user.getUsername(), String.format("User `%s` created.", user.getUsername()));
    }

    private void setUser(User user, JSONObject userJson) {
        String nickname = userJson.optString("nickname");
        if (nickname.isEmpty())
            user.setNickname(user.getUsername());
        else
            user.setNickname(nickname);

        user.setIconUrl(userJson.optString("iconUrl"));
        user.setEmail(userJson.optString("email"));

        String gender = userJson.optString("gender");
        if (gender.isEmpty() || gender.equals("Male")) {
            user.setGender(User.Gender.Male);
        } else if (gender.equals("Female")) {
            user.setGender(User.Gender.Female);
        } else {
            throw new IllegalArgumentException("Gender of given user info must be `Male` or `Female`.");
        }

        String birthDateStr = userJson.optString("birthDate");
        if (birthDateStr.isEmpty()) {
            user.setBirthDate(LocalDate.now());
        } else {
            try {
                user.setBirthDate(LocalDate.parse(birthDateStr, formatter));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Birth date of given user info must be in the format of `MM/dd/yyyy`.");
            }
        }
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }
}
