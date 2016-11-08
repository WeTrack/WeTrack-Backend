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
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;

public class NotificationService extends AbstractWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private static final String TOKEN_PREFIX = "Token:";

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    private BiMap<WebSocketSession, String> sessionUsername = HashBiMap.create();

    private WebSocketMessage<String> sessionHello
            = new TextMessage("Hello, anonymous user! Please provide your token for authentication.");
    private WebSocketMessage<String> invalidMessage
            = new TextMessage("The message is not in valid format. Please try again.");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(sessionHello);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String message = textMessage.getPayload();
        if (LOG.isDebugEnabled()) {
            if (sessionUsername.containsKey(session))
                LOG.debug("Received message `{}` from user `{}`.", message, sessionUsername.get(session));
            else
                LOG.debug("Received message `{}` from anonymous session `{}`.", message, session.hashCode());
        }
        if (message.startsWith(TOKEN_PREFIX)) {
            LOG.debug("Token messaged detected! Handling to corresponding method...");
            onTokenAuthenticate(message.substring(TOKEN_PREFIX.length()), session);
            return;
        }
        Notification notification;
        try {
            notification = gson.fromJson(message, Notification.class);
        } catch (JsonParseException ex) {
            session.sendMessage(invalidMessage);
            return;
        }
        if (notification instanceof ChatMessage) {
            onChatMessage((ChatMessage) notification, session);
            return;
        }
        session.sendMessage(invalidMessage);
    }

    private void onChatMessage(ChatMessage message, WebSocketSession session) throws Exception {
        if (!sessionUsername.containsKey(session)) {
            session.sendMessage(response(401, "You must authenticate before sending any message."));
            return;
        }

        String authenticatedUsername = sessionUsername.get(session);
        Chat chat = chatRepository.findById(message.getChatId());
        if (chat == null) {
            session.sendMessage(response(404, "Chat with given ID `" + message.getChatId() + "` does not exist."));
            return;
        }
        if (!chat.getMemberNames().contains(authenticatedUsername)) {
            session.sendMessage(response(401, "You are not a member of this group."));
            return;
        }

        message.setFromUsername(sessionUsername.get(session));
        message.setSendTime(LocalDateTime.now());
        chatMessageRepository.insert(message);

        for (String memberName : chat.getMemberNames()) {
            WebSocketSession memberSession = sessionUsername.inverse().get(memberName);
            if (memberSession != null)
                memberSession.sendMessage(new TextMessage(gson.toJson(message)));
        }

        session.sendMessage(response(200, "Message Sent."));
    }

    private void onTokenAuthenticate(String token, WebSocketSession session) throws Exception {
        LOG.debug("Received token `{}` from session `{}`", token, session.hashCode());
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.debug("Token is invalid or has expired. Returning 401 Unauthorized...");
            session.sendMessage(response(401, "The given token `" + token + "` is invalid or has expired."));
            return;
        }

        String username = tokenInDB.getUsername();
        if (sessionUsername.containsValue(username)) {
            LOG.debug("Token has already logged in another session. Logging out the old session...");
            WebSocketSession oldSession = sessionUsername.inverse().get(tokenInDB.getUsername());
            oldSession.sendMessage(response(401, "You has logged in another session."));
        }

        sessionUsername.forcePut(session, username);
        LOG.debug("Token authenticated. Returning 200 OK...");
        session.sendMessage(response(200, "Authentication successful. Welcome, " + username + "."));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (sessionUsername.containsKey(session))
            LOG.info("WebSocket session of user `" + sessionUsername.get(session)
                    + "` closed: " + status.toString());
        else
            LOG.info("Anonymous WebSocket session `" + session.hashCode() + "` closed: " + status.toString());
    }

    private TextMessage response(int statusCode, String message) {
        return new TextMessage(gson.toJson(new Message(statusCode, message)));
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    public void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void setChatMessageRepository(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
}
