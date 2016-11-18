package com.wetrack.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wetrack.dao.ChatMessageRepository;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static com.wetrack.util.ResponseUtils.*;

@Path("/chats/{chatId}/messages")
@Produces(MediaType.APPLICATION_JSON)
public class ChatMessageService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageService.class);

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    @GET
    public Response getChatMessage(@PathParam("chatId") String chatId,
                                   @QueryParam("token") @DefaultValue("") String token,
                                   @QueryParam("since") @DefaultValue("") String sinceTimeStr,
                                   @QueryParam("before") @DefaultValue("") String beforeTimeStr,
                                   @QueryParam("limit") @DefaultValue("50") int limit) {
        LOG.debug("GET  /chats/{}/messages", chatId);

        if (!chatRepository.chatExists(chatId))
            return notFound("Chat with ID `" + chatId + "` does not exist.");
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!chatRepository.isMember(chatId, tokenInDB.getUsername()))
            return unauthorized("You are not a member of this chat.");

        LocalDateTime sinceTime = parseTime(sinceTimeStr);
        LocalDateTime beforeTime = parseTime(beforeTimeStr);
        if (beforeTime == null)
            beforeTime = LocalDateTime.now();
        if (sinceTime == null)
            return ok(gson.toJson(chatMessageRepository.getChatMessages(chatId, beforeTime, limit)));
        return ok(gson.toJson(chatMessageRepository.getChatMessages(chatId, sinceTime, beforeTime)));
    }

    @DELETE
    @Path("/{messageId}")
    public Response withdrawMessage(@PathParam("chatId") String chatId,
                                    @PathParam("messageId") String messageId,
                                    @QueryParam("token") @DefaultValue("") String token) {
        LOG.debug("DELETE /chats/{}/messages", chatId);

        if (!chatRepository.chatExists(chatId))
            return notFound("Chat with ID `" + chatId + "` does not exist.");
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!chatRepository.isMember(chatId, tokenInDB.getUsername()))
            return unauthorized("You are not a member of this chat.");

        ChatMessage message = chatMessageRepository.findById(messageId);
        if (message == null)
            return notFound("Message with ID `" + messageId + "` cannot be found.");
        if (!message.getFromUsername().equals(tokenInDB.getUsername()))
            return unauthorized("You cannot withdraw other's message.");
        if (message.getSendTime().isBefore(LocalDateTime.now().minusMinutes(3)))
            return forbidden("You cannot withdraw message sent at more than 3 minutes earlier.");

        chatMessageRepository.delete(message);
        return okMessage("Message withdrawn.");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@PathParam("chatId") String chatId,
                                @QueryParam("token") @DefaultValue("") String token,
                                @DefaultValue("") String requestBody) {
        LOG.debug("POST /chats/{}/messages", chatId);

        if (!chatRepository.chatExists(chatId))
            return notFound("Chat with ID `" + chatId + "` does not exist.");
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return unauthorized("The given token is invalid or has expired. Please log in again.");
        if (!chatRepository.isMember(chatId, tokenInDB.getUsername()))
            return unauthorized("You are not a member of this chat.");

        if (requestBody.trim().isEmpty())
            return badRequest("The request body cannot be empty.");

        ChatMessage message;
        try {
            message = gson.fromJson(requestBody, ChatMessage.class);
        } catch (JsonSyntaxException ex) {
            return badRequest("The given request body is not in valid JSON format.");
        }

        message.setChatId(chatId);
        message.setFromUsername(tokenInDB.getUsername());
        message.setSendTime(LocalDateTime.now());
        chatMessageRepository.insert(message);
        return created("/chats/" + chatId + "/messages/" + message.getId(), "Message sent.");
    }

    private LocalDateTime parseTime(String timeStr) {
        try {
            return LocalDateTime.parse(timeStr);
        } catch (DateTimeParseException ex) {
            // Ignore
        }
        return null;
    }
}
