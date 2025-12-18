package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatMessageEntity;
import com.example.chat.model.TypingMessage;
import com.example.chat.repository.ChatMessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final SimpMessagingTemplate template;
    private final ChatMessageRepository repository;

    public MessageController(SimpMessagingTemplate template, ChatMessageRepository repository) {
        this.template = template;
        this.repository = repository;
    }

    @MessageMapping("/chat.message")
    public void handleMessage(ChatMessage msg) {
        if (msg == null) return;

        if (msg.getTimestamp() == null) msg.setTimestamp(System.currentTimeMillis());
        if (msg.getType() == null) msg.setType(ChatMessage.Type.TALK);
        if (msg.getSender() == null || msg.getSender().isBlank()) msg.setSender("anonymous");
        if (msg.getRoomId() == null || msg.getRoomId().isBlank()) msg.setRoomId("room1");

        if (msg.getType() != ChatMessage.Type.TYPING) {
            repository.save(new ChatMessageEntity(
                    msg.getRoomId(),
                    msg.getSender(),
                    msg.getType().name(),
                    msg.getContent() == null ? "" : msg.getContent(),
                    msg.getTimestamp()
            ));
        }

        template.convertAndSend("/topic/room/" + msg.getRoomId(), msg);
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(TypingMessage typing) {
        if (typing == null) return;
        if (typing.getTimestamp() == null) typing.setTimestamp(System.currentTimeMillis());
        if (typing.getSender() == null || typing.getSender().isBlank()) typing.setSender("anonymous");
        if (typing.getRoomId() == null || typing.getRoomId().isBlank()) typing.setRoomId("room1");

        template.convertAndSend("/topic/typing/" + typing.getRoomId(), typing);
    }
}
