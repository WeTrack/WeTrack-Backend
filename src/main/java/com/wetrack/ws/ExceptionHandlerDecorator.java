package com.wetrack.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import static com.wetrack.ws.WsResponse.internalError;

public class ExceptionHandlerDecorator extends WebSocketHandlerDecorator {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerDecorator.class);

    public ExceptionHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            getDelegate().afterConnectionEstablished(session);
        }
        catch (Throwable ex) {
            LOG.warn("Exception occurred when session `" + session + "` is established: ", ex);
            trySendMessage(session, internalError("Exception occurred when the server is establishing this session: "
                    + ex.getClass().getName()));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            getDelegate().handleMessage(session, message);
        }
        catch (Throwable ex) {
            LOG.warn("Exception occurred when handling message for session `" + session + "`: ", ex);
            trySendMessage(session, internalError("Exception occurred when the server is handling message for this session: "
                    + ex.getClass().getName()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            getDelegate().handleTransportError(session, exception);
        }
        catch (Throwable ex) {
            LOG.warn("Exception occurred when handling transport exception for session `" + session + "`: ", ex);
            trySendMessage(session, internalError("Exception occurred when the server is handling transport exception for this session: "
                    + ex.getClass().getName()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        try {
            getDelegate().afterConnectionClosed(session, closeStatus);
        }
        catch (Throwable ex) {
            LOG.warn("Exception occurred when closing session on status `" + closeStatus + "`: ", ex);
        }
    }

    private void trySendMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            session.sendMessage(message);
        } catch (Exception ex) {
            LOG.warn("Exception occurred when trying to send message to session `" + session + "`: ", ex);
            try {
                if (session.isOpen())
                    session.close(new CloseStatus(1011, "Server error: " + ex.getClass().getName()));
            } catch (Exception e) {}
        }
    }
}
