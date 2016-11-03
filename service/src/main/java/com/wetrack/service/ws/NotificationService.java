package com.wetrack.service.ws;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.wetrack.dao.ChatMessageRepository;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;

@ServerEndpoint("/notifications")
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private static final String TOKEN_PREFIX = "Token:";

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    private BiMap<Session, String> sessionUsername = HashBiMap.create();

    @OnOpen
    public void openSession(Session session) {
        try {
            session.getBasicRemote().sendText("Hello, anonymous user! Please provide your token for authentication.");
        } catch (IOException ex) {
            LOG.warn("Exception occurred when a new session open: ", ex);
        }
    }

    @OnMessage
    public String receiveMessage(String message, Session session) {
        if (message.startsWith(TOKEN_PREFIX))
            return onTokenAuthenticate(message.substring(TOKEN_PREFIX.length()), session);
        Notification notification;
        try {
            notification = gson.fromJson(message, Notification.class);
        } catch (JsonParseException ex) {
            return "The message is not in valid format. Please try again.";
        }
        if (notification instanceof ChatMessage)
            return onChatMessage((ChatMessage) notification, session);
        return "The message is not in valid format. Please try again.";
    }

    private String onChatMessage(ChatMessage message, Session session) {
        if (!sessionUsername.containsKey(session))
            return response(401, "You must authenticate before sending any message.");

        String authenticatedUsername = sessionUsername.get(session);
        Chat chat = chatRepository.findById(message.getChatId());
        if (chat == null)
            return response(404, "Chat with given ID `" + message.getChatId() + "` does not exist.");
        if (!chat.getMemberNames().contains(authenticatedUsername))
            return response(401, "You are not a member of this group.");

        message.setFromUsername(sessionUsername.get(session));
        message.setSendTime(LocalDateTime.now());
        chatMessageRepository.insert(message);

        for (String memberName : chat.getMemberNames()) {
            Session memberSession = sessionUsername.inverse().get(memberName);
            if (memberSession != null)
                sendText(memberSession, gson.toJson(message));
        }

        return response(200, "Message sent.");
    }

    private String onTokenAuthenticate(String token, Session session) {
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now()))
            return response(401, "The given token `" + token + "` is invalid or has expired.");

        String username = tokenInDB.getUsername();
        if (sessionUsername.containsValue(username)) {
            Session oldSession = sessionUsername.inverse().get(tokenInDB.getUsername());
            sendText(oldSession, response(401, "You has logged in another session."));
        }

        sessionUsername.forcePut(session, username);
        return response(200, "Authentication successful. Welcome, " + username + ".");
    }

    @OnClose
    public void closeSession(Session session, CloseReason closeReason) {
        if (sessionUsername.containsKey(session))
            LOG.info("WebSocket session of user `" + sessionUsername.get(session)
                    + "` closed: " + closeReason.toString());
        else
            LOG.info("Anonymous WebSocket session `" + session.hashCode() + "` closed: " + closeReason.toString());
    }

    private void sendText(Session session, String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException ex) {
            LOG.warn("Exception occurred when trying to send text to client: ", ex);
        }
    }

    private String response(int statusCode, String message) {
        return gson.toJson(new Message(statusCode, message));
    }
}
