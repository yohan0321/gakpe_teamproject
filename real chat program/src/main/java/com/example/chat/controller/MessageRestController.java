package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatMessageEntity;
import com.example.chat.repository.ChatMessageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessageRestController {

    private final ChatMessageRepository repository;

    public MessageRestController(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/messages")
    public List<ChatMessage> getMessages(@RequestParam("roomId") String roomId,
                                         @RequestParam(name="limit", required=false, defaultValue="50") int limit) {

        int effectiveLimit = Math.max(1, Math.min(limit, 200));
        List<ChatMessageEntity> entities = repository.findTop100ByRoomIdOrderByTimestampDesc(roomId);
        if (entities == null || entities.isEmpty()) return List.of();

        return entities.stream()
                .sorted(Comparator.comparingLong(e -> e.getTimestamp() != null ? e.getTimestamp() : 0L))
                .limit(effectiveLimit)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ChatMessage toDto(ChatMessageEntity e) {
        ChatMessage dto = new ChatMessage();
        dto.setRoomId(e.getRoomId());
        dto.setSender(e.getSender());
        dto.setContent(e.getContent());
        dto.setTimestamp(e.getTimestamp());
        try { dto.setType(ChatMessage.Type.valueOf(e.getType())); }
        catch (Exception ex) { dto.setType(ChatMessage.Type.TALK); }
        return dto;
    }
}
