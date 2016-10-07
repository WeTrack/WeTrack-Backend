package com.wetrack.service;

import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.temporal.ChronoUnit;

import static com.wetrack.util.RsResponseUtils.badRequest;
import static com.wetrack.util.RsResponseUtils.forbidden;
import static com.wetrack.util.RsResponseUtils.ok;

@Produces(MediaType.APPLICATION_JSON)
public class AuthenService {

    private UserRepository userRepository;
    private UserTokenRepository userTokenRepository;

    @POST
    @Path("/users/{username}/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response userLogin(@PathParam("username") @DefaultValue("") String username,
                              @FormParam("password") @DefaultValue("") String password) {
        if (username.isEmpty())
            return badRequest("The given username cannot be empty.");
        if (password.isEmpty())
            return badRequest("The given password cannot be empty.");

        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password))
            return forbidden("The given username or password is incorrect.");

        UserToken token = userTokenRepository.findByUsername(username);
        if (token == null) {
            token = new UserToken(username, 1, ChronoUnit.DAYS);
            userTokenRepository.insert(token);
        }

        JSONObject result = new JSONObject();
        result.append("token", token.getToken()).append("expireTime", token.getExpireTime());

        return ok(result);
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }
}
