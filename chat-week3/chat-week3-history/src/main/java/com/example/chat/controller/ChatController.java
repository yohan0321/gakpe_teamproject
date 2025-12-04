package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatMessageEntity;
import com.example.chat.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository repository) {
        this.messagingTemplate = messagingTemplate;
        this.repository = repository;
    }

    @MessageMapping("/chat.send")
    public void send(@Payload ChatMessage message) {
        validate(message, true);
        long now = System.currentTimeMillis();
        message.setTimestamp(now);

        ChatMessageEntity entity = new ChatMessageEntity(
                message.getRoomId(),
                message.getSender(),
                message.getType() != null ? message.getType().name() : "TALK",
                message.getContent(),
                now
        );
        repository.save(entity);

        String destination = "/topic/room/" + message.getRoomId();
        log.debug("SEND to {} from {}: {}", destination, message.getSender(), message.getContent());
        messagingTemplate.convertAndSend(destination, message);
    }

    @MessageMapping("/chat.join")
    public void join(@Payload ChatMessage message) {
        validate(message, false);
        long now = System.currentTimeMillis();
        message.setTimestamp(now);
        message.setType(ChatMessage.Type.ENTER);
        message.setContent(message.getSender() + " 님이 입장했습니다.");

        ChatMessageEntity entity = new ChatMessageEntity(
                message.getRoomId(),
                message.getSender(),
                "ENTER",
                message.getContent(),
                now
        );
        repository.save(entity);

        String destination = "/topic/room/" + message.getRoomId();
        log.debug("JOIN to {} by {}", destination, message.getSender());
        messagingTemplate.convertAndSend(destination, message);
    }

    private void validate(ChatMessage message, boolean requireContent) {
        if (message == null) throw new IllegalArgumentException("메시지가 비어 있습니다.");
        if (!StringUtils.hasText(message.getRoomId())) throw new IllegalArgumentException("roomId가 비어 있습니다.");
        if (!StringUtils.hasText(message.getSender())) throw new IllegalArgumentException("sender가 비어 있습니다.");
        if (requireContent && !StringUtils.hasText(message.getContent())) throw new IllegalArgumentException("content가 비어 있습니다.");
    }
}
