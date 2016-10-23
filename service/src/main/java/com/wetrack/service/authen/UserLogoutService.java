package com.wetrack.service.authen;

import com.wetrack.dao.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.wetrack.util.RsResponseUtils.badRequest;
import static com.wetrack.util.RsResponseUtils.ok;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON)
public class UserLogoutService {
    private static final Logger LOG = LoggerFactory.getLogger(UserLogoutService.class);

    @Autowired private UserTokenRepository userTokenRepository;

    @POST
    public Response userLogout(String token) {
        LOG.debug("POST /logout");

        if (token == null || token.trim().isEmpty()) {
            LOG.debug("The received token is empty. Returning `400 Bad Request`...");
            return badRequest("Token must be provided in the request body to log out.");
        }

        userTokenRepository.deleteByTokenStr(token);
        return ok();
    }

}
