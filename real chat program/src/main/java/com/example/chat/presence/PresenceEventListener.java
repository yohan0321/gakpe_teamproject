package com.example.chat.presence;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class PresenceEventListener {

    private final PresenceRegistry registry;

    public PresenceEventListener(PresenceRegistry registry) {
        this.registry = registry;
    }

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = acc.getSessionId();

        String roomId = acc.getFirstNativeHeader("X-ROOMID");
        String username = acc.getFirstNativeHeader("X-USERNAME");

        Map<String, Object> attrs = acc.getSessionAttributes();
        if (attrs != null) {
            if (roomId == null) roomId = (String) attrs.get("roomId");
            if (username == null) username = (String) attrs.get("username");
            if (roomId != null) attrs.put("roomId", roomId);
            if (username != null) attrs.put("username", username);
        }

        if (sessionId == null || roomId == null || username == null) return;
        registry.add(roomId, sessionId, username);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = acc.getSessionId();
        Map<String, Object> attrs = acc.getSessionAttributes();
        if (attrs == null || sessionId == null) return;

        String roomId = (String) attrs.get("roomId");
        if (roomId == null) return;

        registry.remove(roomId, sessionId);
    }
}
