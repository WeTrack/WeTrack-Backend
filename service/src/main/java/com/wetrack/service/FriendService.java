package com.wetrack.service;

import com.google.gson.Gson;
import com.wetrack.dao.FriendRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Friend;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.wetrack.util.ResponseUtils.*;

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

        List<User> friends = new ArrayList<>(friend.getFriendNames().size());
        List<String> deletedNames = new LinkedList<>();
        for (String friendName : friend.getFriendNames()) {
            User user = userRepository.findByUsername(friendName);
            if (user == null) {
                LOG.warn("Friend `" + friendName + "` no longer exists.");
                deletedNames.add(friendName);
            } else
                friends.add(user);
        }

        if (friend.getFriendNames().removeAll(deletedNames))
            friendRepository.update(friend);

        return ok(gson.toJson(friends));
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
        if (token.trim().isEmpty())
            return badRequest("Token must be provided as query parameter.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot modify others' friend list.");

        Friend friend = friendRepository.findById(username);
        if (friend == null)
            friend = new Friend(username);
        friend.getFriendNames().add(friendName);
        friendRepository.insert(friend);

        friend = friendRepository.findById(friendName);
        if (friend == null)
            friend = new Friend(friendName);
        friend.getFriendNames().add(username);
        friendRepository.insert(friend);

        return okMessage("You have added `" + friendName + "` as your friend.");
    }

    @DELETE
    @Path("/{friendName}")
    public Response deleteFriend(@PathParam("username") String username,
                                 @PathParam("friendName") @DefaultValue("") String friendName,
                                 @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /users/{}/friends/{}", username, friendName);

        if (token.trim().isEmpty())
            return badRequest("Token must be provided as query parameter.");

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
        if (friend == null) {
            friend = new Friend(username);
            friendRepository.insert(friend);
        }
        if (!friend.getFriendNames().remove(friendName))
            return notFound("User with username `" + friendName + "` is not your friend.");
        friendRepository.update(friend);

        friend = friendRepository.findById(friendName);
        if (friend == null) {
            friend = new Friend(friendName);
            friendRepository.insert(friend);
        }
        if (friend.getFriendNames().remove(username))
            friendRepository.update(friend);

        return okMessage("You have deleted `" + friendName + "` from your friend list.");
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
        return friend.getFriendNames().contains(friendName) ? ok() : notFound();
    }

}
