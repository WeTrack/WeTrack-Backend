package com.wetrack.ws;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wetrack.json.LocalDateTimeTypeAdapter;
import com.wetrack.model.ChatMessage;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;

abstract class WsResponse {
    static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    static final String TYPE_CHAT_MESSAGE = "chat_message";
    static final String TYPE_CHAT_MESSAGE_ACK = "chat_message_ack";
    static final String TYPE_WS_MESSAGE = "message";

    private static final int HELLO = 1000;
    private static final int TOKEN_VERIFIED = 1001;
    private static final int INVALID_MESSAGE = 2000;
    private static final int NOT_AUTHENTICATED = 2001;
    private static final int NOT_CHAT_MEMBER = 2002;
    private static final int INVALID_CHAT_ID = 2003;
    private static final int INVALID_TOKEN = 2004;
    private static final int TOKEN_USED_IN_OTHER_SESSION = 2005;
    private static final int INTERNAL_ERROR = 3000;

    static TextMessage hello(String message) {
        return jsonMessage(new WsMessage(HELLO, message));
    }

    static TextMessage internalError(String message) {
        return jsonMessage(new WsMessage(INTERNAL_ERROR, message));
    }

    static TextMessage chatMessage(ChatMessage message) {
        return new TextMessage(TYPE_CHAT_MESSAGE + gson.toJson(message));
    }

    static TextMessage invalidMessage(String message) {
        return jsonMessage(new WsMessage(INVALID_MESSAGE, message));
    }

    static TextMessage messageAck(String messageId, LocalDateTime sendTime) {
        return new TextMessage(TYPE_CHAT_MESSAGE_ACK + gson.toJson(new ChatMessageAck(messageId, sendTime)));
    }

    static TextMessage notAuthenticated(String message) {
        return jsonMessage(new WsMessage(NOT_AUTHENTICATED, message));
    }

    static TextMessage notChatMember(String message) {
        return jsonMessage(new WsMessage(NOT_CHAT_MEMBER, message));
    }

    static TextMessage invalidChatId(String message) {
        return jsonMessage(new WsMessage(INVALID_CHAT_ID, message));
    }

    static TextMessage invalidToken(String message) {
        return jsonMessage(new WsMessage(INVALID_TOKEN, message));
    }

    static TextMessage tokenUsedInOtherSession(String message) {
        return jsonMessage(new WsMessage(TOKEN_USED_IN_OTHER_SESSION, message));
    }

    static TextMessage tokenVerified(String message) {
        return jsonMessage(new WsMessage(TOKEN_VERIFIED, message));
    }

    private static <T> TextMessage jsonMessage(T payload) {
        return new TextMessage(TYPE_WS_MESSAGE + gson.toJson(payload));
    }
}
