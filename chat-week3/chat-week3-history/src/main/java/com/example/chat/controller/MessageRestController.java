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

    /**
     * 최근 대화 히스토리 조회
     * 예: /api/messages?roomId=room1&limit=50
     */
    @GetMapping("/api/messages")
    public List<ChatMessage> getMessages(
    		@RequestParam("roomId") String roomId,
    		@RequestParam(name = "limit", required = false, defaultValue = "50") int limit

    ) {
        // limit 보호 (1~200 사이로 강제)
        int effectiveLimit = Math.max(1, Math.min(limit, 200));

        // DB에서 최근 100개까지 가져오기 (최신순)
        List<ChatMessageEntity> entities =
                repository.findTop100ByRoomIdOrderByTimestampDesc(roomId);

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        // timestamp null 방어 + 오래된 것부터 정렬 + limit 적용 + DTO 변환
        return entities.stream()
                .sorted(Comparator.comparingLong(e ->
                        e.getTimestamp() != null ? e.getTimestamp() : 0L))
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

        // type 문자열이 이상하거나 null이어도 TALK로 fallback
        try {
            if (e.getType() != null) {
                dto.setType(ChatMessage.Type.valueOf(e.getType()));
            } else {
                dto.setType(ChatMessage.Type.TALK);
            }
        } catch (Exception ex) {
            dto.setType(ChatMessage.Type.TALK);
        }

        return dto;
    }
}
