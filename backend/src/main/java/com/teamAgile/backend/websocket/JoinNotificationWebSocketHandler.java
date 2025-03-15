package com.teamAgile.backend.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JoinNotificationWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("HELLO");
        // Create a join notification message
        JoinNotificationMessage message = new JoinNotificationMessage(
                "USER_JOINED",
                "A new user has joined",
                session.getId());

        // Broadcast to all connected clients except the one who just joined
        broadcastMessage(message, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // For now, we're not handling incoming messages
        // This could be extended to handle custom messages from clients
    }

    /**
     * Broadcasts a message to all connected WebSocket sessions except the sender
     * 
     * @param message  The message to broadcast
     * @param senderId The ID of the sender to exclude from broadcast
     */
    public void broadcastMessage(JoinNotificationMessage message, String excludeSessionId) {
        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);

            sessions.forEach((sessionId, session) -> {
                // Don't send the message back to the sender
                if (!sessionId.equals(excludeSessionId) && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(messageJson));
                    } catch (IOException e) {
                        // Log the error but continue with other sessions
                        System.err.println("Error sending message to session " + sessionId + ": " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all connected WebSocket sessions
     * 
     * @param message The message to broadcast
     */
    public void broadcastMessageToAll(JoinNotificationMessage message) {
        broadcastMessage(message, null);
    }
}