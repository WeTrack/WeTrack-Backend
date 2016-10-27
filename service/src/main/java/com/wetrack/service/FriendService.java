package com.wetrack.service;

import com.google.gson.Gson;
import com.wetrack.dao.FriendRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Friend;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users/{username}/friends")
@Produces(MediaType.APPLICATION_JSON)
public class FriendService {
    private static final Logger LOG = LoggerFactory.getLogger(FriendService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private FriendRepository friendRepository;

    @GET
    public Response getFriends(@PathParam("username") String username,
                               @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("GET  /users/{}/friends", username);

        if (userRepository.countByUsername(username) == 0)
            return notFound("User with given username does not exist.");

        if (token.isEmpty())
            return badRequest("Token must be provided in the query param");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot query for others' friend list.");

        Friend friend = friendRepository.findById(username);
        if (friend == null) {
            friend = new Friend(username);
            friendRepository.insert(friend);
        }

        return ok(gson.toJson(friend.getFriends()));
    }

    @POST
    @Path("/{friendName}")
    public Response addFriend(@PathParam("username") String username,
                              @PathParam("friendName") @DefaultValue("") String friendName,
                              @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("POST /users/{}/friends/{}", username, friendName);

        if (userRepository.countByUsername(username) == 0)
            return notFound("User with given username `" + username + "` does not exist.");
        if (userRepository.countByUsername(friendName) == 0)
            return notFound("User with given username `" + friendName + "` does not exist.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot modify others' friend list.");

        Friend friend = friendRepository.findById(username);
        if (friend == null)
            friend = new Friend(username);
        friend.getFriends().add(userRepository.findByUsername(friendName));
        friendRepository.insert(friend);

        friend = friendRepository.findById(friendName);
        if (friend == null)
            friend = new Friend(friendName);
        friend.getFriends().add(userRepository.findByUsername(username));
        friendRepository.insert(friend);

        return ok();
    }

    @DELETE
    @Path("/{friendName}")
    public Response deleteFriend(@PathParam("username") String username,
                                 @PathParam("friendName") @DefaultValue("") String friendName,
                                 @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /users/{}/friends/{}", username, friendName);

        if (userRepository.countByUsername(username) == 0)
            return notFound("User with given username `" + username + "` does not exist.");
        if (userRepository.countByUsername(friendName) == 0)
            return notFound("User with given username `" + friendName + "` does not exist.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot modify others' friend list.");

        Friend friend = friendRepository.findById(username);
        friend.getFriends().removeIf((u) -> u.getUsername().equals(friendName));
        friendRepository.update(friend);

        friend = friendRepository.findById(friendName);
        friend.getFriends().removeIf((u) -> u.getUsername().equals(username));
        friendRepository.update(friend);

        return ok();
    }

    @HEAD
    @Path("/{friendName}")
    public Response isFriend(@PathParam("username") String username,
                             @PathParam("friendName") @DefaultValue("") String friendName) {
        LOG.debug("HEAD /users/{}/friends/{}", username, friendName);

        if (userRepository.countByUsername(username) == 0)
            return notFound();
        if (userRepository.countByUsername(friendName) == 0)
            return notFound();

        Friend friend = friendRepository.findById(username);
        long count = friend.getFriends().stream().filter((u) -> u.getUsername().equals(friendName)).count();
        if (count == 0)
            return notFound();
        return ok();
    }

}
