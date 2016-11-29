package com.wetrack.ws;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.wetrack.dao.ChatMessageRepository;
import com.wetrack.dao.ChatRepository;
import com.wetrack.dao.UserTokenRepository;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.UserToken;
import com.wetrack.util.CryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;

import static com.wetrack.ws.WsResponse.*;

public class WebSocketService extends AbstractWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketService.class);

    private static final String TOKEN_PREFIX = "Token:";

    @Autowired private Gson gson;
    @Autowired private UserTokenRepository userTokenRepository;
    @Autowired private ChatRepository chatRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    private BiMap<WebSocketSession, String> sessionUsername = HashBiMap.create();

    private WebSocketMessage<String> sessionHello
            = hello("Hello, anonymous user! Please provide your token for authentication.");
    private WebSocketMessage<String> invalidMessage = invalidMessage("The message is not in valid format.");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.debug("WebSocket session established with `{}:{}`",
                session.getRemoteAddress().getAddress(), session.getRemoteAddress().getPort());
        sendMessage(session, sessionHello);
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

        if (message.startsWith(WsResponse.TYPE_CHAT_MESSAGE)) {
            ChatMessage chatMessage;
            try {
                chatMessage = gson.fromJson(message.substring(WsResponse.TYPE_CHAT_MESSAGE.length()), ChatMessage.class);
            } catch (Exception ex) {
                sendMessage(session, invalidMessage);
                return;
            }
            onChatMessage(chatMessage, session);
            return;
        }

        sendMessage(session, invalidMessage);
    }

    private void onChatMessage(ChatMessage message, WebSocketSession session) throws Exception {
        if (!sessionUsername.containsKey(session)) {
            sendMessage(session, notAuthenticated("You must log in first."));
            return;
        }

        String authenticatedUsername = sessionUsername.get(session);
        Chat chat = chatRepository.findById(message.getChatId());
        if (chat == null) {
            sendMessage(session, invalidChatId("Chat with given ID `" + message.getChatId() + "` does not exist."));
            return;
        }
        if (!chat.getMemberNames().contains(authenticatedUsername)) {
            sendMessage(session, notChatMember("You are not a member of this chat."));
            return;
        }

        String providedId = message.getId();
        message.setId(CryptoUtils.md5Digest(String.format("%s:%s:%s", message.getChatId(), message.getFromUsername(), message.getSendTime().toString())));
        message.setFromUsername(sessionUsername.get(session));
        message.setSendTime(LocalDateTime.now());
        chatMessageRepository.insert(message);

        session.sendMessage(messageAck(providedId, message.getSendTime()));

        for (String memberName : chat.getMemberNames()) {
            if (memberName.equals(authenticatedUsername))
                continue;
            WebSocketSession memberSession = sessionUsername.inverse().get(memberName);
            if (memberSession != null)
                sendMessage(memberSession, chatMessage(message));
        }
    }

    private void onTokenAuthenticate(String token, WebSocketSession session) throws Exception {
        LOG.debug("Received token `{}` from session `{}`", token, session.hashCode());
        UserToken tokenInDB = userTokenRepository.findByTokenStr(token);
        if (tokenInDB == null || tokenInDB.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.debug("Token is invalid or has expired. Returning 401 Unauthorized...");
            session.sendMessage(invalidToken("The given token `" + token + "` is invalid or has expired."));
            return;
        }

        String username = tokenInDB.getUsername();
        if (sessionUsername.containsValue(username)) {
            LOG.debug("Token has already logged in another session. Logging out the old session...");
            WebSocketSession oldSession = sessionUsername.inverse().get(tokenInDB.getUsername());
            if (oldSession != session)
                sendMessage(oldSession, tokenUsedInOtherSession("You has logged in on another session."));
        }

        sessionUsername.forcePut(session, username);
        LOG.debug("Token authenticated. User `{}` logged in on session `{}`.", username, session.hashCode());
        sendMessage(session, tokenVerified("Authentication successful. Welcome, " + username + "."));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (sessionUsername.containsKey(session))
            LOG.info("WebSocket session of user `" + sessionUsername.get(session)
                    + "` closed: " + status.toString());
        else
            LOG.info("Anonymous WebSocket session `" + session.hashCode() + "` closed: " + status.toString());
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            session.sendMessage(message);
        } catch (Throwable ex) {
            // Exception occurred when trying to send message to the session.
            // Close and unregister the session.
            tryCloseWithError(session, ex);
            sessionUsername.remove(session);
        }
    }

    private void tryCloseWithError(WebSocketSession session, Throwable exception) {
        LOG.debug("Closing session `" + session.hashCode() + "` due to exception: ", exception);
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (Throwable ex) {}
        }
    }
}
