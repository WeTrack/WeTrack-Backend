package com.wetrack.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.FriendRepository;
import com.wetrack.dao.UserRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.json.GsonTypes;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import org.apache.commons.lang3.StringUtils;
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

@Path("/chats")
@Produces(MediaType.APPLICATION_JSON)
public class ChatService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    @Autowired private Gson gson;
    @Autowired private UserRepository userRepository;
    @Autowired private FriendRepository friendRepository;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createChat(@QueryParam("token") @DefaultValue("") String token,
                               @DefaultValue("") String requestBody) {
        LOG.debug("POST /chats");
        if (token.trim().isEmpty())
            return badRequest("Token must be provided as query parameter.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        try {
            ChatCreateRequest request = gson.fromJson(requestBody, ChatCreateRequest.class);
            if (request.name == null || request.name.trim().isEmpty())
                return badRequest("Name of the chat must be provided in the request body.");
            if (request.memberNames == null || request.memberNames.isEmpty())
                return badRequest("A chat must contain at least two people.");

            Chat chat = new Chat(request.name);
            for (String memberName : request.memberNames) {
                if (userRepository.countByUsername(memberName) == 0)
                    return notFound("User with given username `" + memberName + "` does not exist.");
                if (!friendRepository.isFriend(tokenInDB.getUsername(), memberName))
                    return forbidden("User with given username `" + memberName + "` is not your friend.");
                chat.getMemberNames().add(memberName);
            }
            chat.getMemberNames().add(tokenInDB.getUsername());
            chatRepository.insert(chat);
            return created("/chats/" + chat.getId(), "Chat `" + chat.getName() + "` created.");
        } catch (JsonSyntaxException | NullPointerException | IllegalStateException | ClassCastException ex) {
            return badRequest("The given request body is not in valid JSON format.");
        }
    }

    @POST
    @Path("/{chatId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMembers(@PathParam("chatId") String chatId,
                               @QueryParam("token") @DefaultValue("") String token,
                               @DefaultValue("") String requestBody) {
        LOG.debug("POST /chats/{}/members", chatId);

        if (token.trim().isEmpty())
            return badRequest("Token must be provided as query parameter.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");
        if (!chat.getMemberNames().contains(tokenInDB.getUsername()))
            return unauthorized("You are not a member of the specified chat.");

        try {
            List<String> newMemberNames = gson.fromJson(requestBody, GsonTypes.stringListType);
            for (String newMemberName : newMemberNames) {
                if (userRepository.countByUsername(newMemberName) == 0)
                    return notFound("User with given username `" + newMemberName + "` does not exist.");
                if (!friendRepository.isFriend(tokenInDB.getUsername(), newMemberName))
                    return forbidden("User with username `" + newMemberName + "` is not your friend.");
                chat.getMemberNames().add(newMemberName);
                chatRepository.update(chat);
            }
            return okMessage("Users " + StringUtils.join(newMemberNames, ", ") + " are added to chat " + chat.getName());
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
        List<User> members = new ArrayList<>(chat.getMemberNames().size());
        List<String> deletedNames = new LinkedList<>();
        for (String memberName : chat.getMemberNames()) {
            User user = userRepository.findByUsername(memberName);
            if (user == null) {
                LOG.warn("Chat member `" + memberName + "` does not exist.");
                deletedNames.add(memberName);
            } else
                members.add(user);
        }

        if (chat.getMemberNames().removeAll(deletedNames))
            chatRepository.update(chat);

        return ok(gson.toJson(members));
    }

    @DELETE
    @Path("/{chatId}/members/{memberName}")
    public Response removeMember(@PathParam("chatId") String chatId,
                                 @PathParam("memberName") @DefaultValue("") String memberName,
                                 @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /chats/{}/members/{}", chatId, memberName);

        if (token.trim().isEmpty())
            return badRequest("Token must be provided as query parameter.");

        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");

        Chat chat = chatRepository.findById(chatId);
        if (chat == null)
            return notFound("Chat with given chat id does not exist.");
        if (!chat.getMemberNames().contains(tokenInDB.getUsername()))
            return unauthorized("You are not a member of the specified chat.");

        chat.getMemberNames().remove(memberName);
        chatRepository.update(chat);
        return okMessage("User " + memberName + " is removed from chat " + chat.getName() + ".");
    }

    private static class ChatCreateRequest {
        private String name;
        @SerializedName("members") private List<String> memberNames;
    }
}
