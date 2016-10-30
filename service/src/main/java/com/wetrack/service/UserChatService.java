package com.wetrack.service;

import com.google.gson.Gson;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Chat;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/users/{username}/chats")
@Produces(MediaType.APPLICATION_JSON)
public class UserChatService {
    private static Logger LOG = LoggerFactory.getLogger(UserChatService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;

    @GET
    public Response getChatList(@PathParam("username") String username,
                                @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("GET  /users/{}/chats", username);

        if (userRepository.countByUsername(username) == 0)
            return notFound("User with given username does not exist.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot query for others' chat list.");

        List<Chat> chats = chatRepository.getChatListByUsername(username);
        return ok(gson.toJson(chats));
    }

    @DELETE
    @Path("/{chatId}")
    public Response exitChat(@PathParam("username") String username,
                             @PathParam("chatId") @DefaultValue("") String chatId,
                             @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /users/{}/chats", username);

        if (userRepository.countByUsername(username) == 0)
            return notFound("User with given username does not exist.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!tokenInDB.getUsername().equals(username))
            return unauthorized("You cannot modify others' chat list.");

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");

        chat.getMembers().removeIf((u) -> u.getUsername().equals(username));
        chatRepository.update(chat);
        return okMessage("You have exited chat `" + chat.getName() + "`.");
    }
}
