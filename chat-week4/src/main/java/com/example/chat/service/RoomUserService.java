package com.example.chat.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomUserService {

    private final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    public void join(String roomId, String user) {
        if (roomId == null || user == null || roomId.isBlank() || user.isBlank()) return;
        roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(user);
    }

    public void leave(String roomId, String user) {
        if (roomId == null || user == null || roomId.isBlank() || user.isBlank()) return;
        Set<String> set = roomUsers.get(roomId);
        if (set != null) {
            set.remove(user);
            if (set.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
    }

    public Set<String> getUsers(String roomId) {
        Set<String> set = roomUsers.get(roomId);
        if (set == null) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }
}
