package com.example.chat.presence;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceRegistry {

    private final Map<String, Map<String, String>> roomToSessionUser = new ConcurrentHashMap<>();

    public void add(String roomId, String sessionId, String username) {
        roomToSessionUser
                .computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(sessionId, username);
    }

    public void remove(String roomId, String sessionId) {
        Map<String, String> map = roomToSessionUser.get(roomId);
        if (map == null) return;
        map.remove(sessionId);
        if (map.isEmpty()) roomToSessionUser.remove(roomId);
    }

    public List<String> listUsers(String roomId) {
        Map<String, String> map = roomToSessionUser.get(roomId);
        if (map == null) return List.of();
        return map.values().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }
}
