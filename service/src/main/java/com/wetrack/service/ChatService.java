package com.wetrack.service;

import com.google.gson.*;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

import static com.wetrack.util.RsResponseUtils.*;

@Path("/chats")
@Produces(MediaType.APPLICATION_JSON)
public class ChatService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createChat(@QueryParam("token") @DefaultValue("") String token,
                               @DefaultValue("") String requestBody) {
        LOG.debug("POST /chats");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        User loggedInUser = userRepository.findByUsername(tokenInDB.getUsername());

        try {
            JsonObject json = gson.fromJson(requestBody, JsonObject.class);
            Chat chat = new Chat(json.getAsString());
            JsonArray memberNames = json.getAsJsonArray("members");
            for (JsonElement memberNameJson : memberNames) {
                String memberName = memberNameJson.getAsString();
                if (userRepository.countByUsername(memberName) == 0)
                    return notFound("User with given username `" + memberName + "` does not exist.");
                chat.getMembers().add(userRepository.findByUsername(memberName));
            }
            chat.getMembers().add(loggedInUser);
            chatRepository.insert(chat);
            return created("/chats/" + chat.getId(), "Chat created.");
        } catch (JsonSyntaxException | IllegalStateException | ClassCastException ex) {
            return badRequest("The given request body is not in valid JSON format.");
        }
    }

    @POST
    @Path("/{chatId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMember(@PathParam("chatId") String chatId,
                              @QueryParam("token") @DefaultValue("") String token,
                              @DefaultValue("") String requestBody) {
        LOG.debug("POST /chats/{}/members", chatId);

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");
        if (chat.getMembers().stream().filter((u) -> u.getUsername().equals(tokenInDB.getUsername())).count() == 0)
            return unauthorized("You are not a member of the specified chat.");

        try {
            JsonObject json = gson.fromJson(requestBody, JsonObject.class);
            String newMemberName = json.get("member").getAsString();
            if (userRepository.countByUsername(newMemberName) == 0)
                return notFound("User with given username `" + newMemberName + "` does not exist.");
            chat.getMembers().add(userRepository.findByUsername(newMemberName));
            chatRepository.update(chat);
            return ok();
        } catch (JsonSyntaxException | IllegalStateException | ClassCastException ex) {
            return badRequest("The given request body is not in valid JSON format.");
        }
    }

    @GET
    @Path("/{chatId}/members")
    public Response getChatMembers(@PathParam("chatId") String chatId) {
        LOG.debug("GET  /chats/{}/members", chatId);

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");

        return ok(gson.toJson(chat.getMembers()));
    }

    @DELETE
    @Path("/{chatId}/members/{memberName}")
    public Response removeMember(@PathParam("chatId") String chatId,
                                 @PathParam("memberName") String memberName,
                                 @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /chats/{}/members/{}", chatId, memberName);

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");
        if (chat.getMembers().stream().filter((u) -> u.getUsername().equals(tokenInDB.getUsername())).count() == 0)
            return unauthorized("You are not a member of the specified chat.");

        chat.getMembers().removeIf((u) -> u.getUsername().equals(memberName));
        chatRepository.update(chat);
        return ok();
    }

}
