package com.example.chat.controller;

import com.example.chat.presence.PresenceRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ParticipantsRestController {

    private final PresenceRegistry registry;

    public ParticipantsRestController(PresenceRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/api/participants")
    public List<String> participants(@RequestParam("roomId") String roomId) {
        return registry.listUsers(roomId);
    }
}
