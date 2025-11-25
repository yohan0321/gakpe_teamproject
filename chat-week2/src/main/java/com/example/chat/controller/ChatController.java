package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void send(@Payload ChatMessage message) {
        validateMessage(message, true);
        message.setTimestamp(System.currentTimeMillis());
        String destination = "/topic/room/" + message.getRoomId();
        log.debug("SEND to {} from {}: {}", destination, message.getSender(), message.getContent());
        messagingTemplate.convertAndSend(destination, message);
    }

    @MessageMapping("/chat.join")
    public void join(@Payload ChatMessage message) {
        validateMessage(message, false);
        message.setTimestamp(System.currentTimeMillis());
        message.setContent(message.getSender() + " 님이 입장했습니다.");
        String destination = "/topic/room/" + message.getRoomId();
        log.debug("JOIN to {} by {}", destination, message.getSender());
        messagingTemplate.convertAndSend(destination, message);
    }

    private void validateMessage(ChatMessage message, boolean requireContent) {
        if (message == null) {
            throw new IllegalArgumentException("메시지가 비어 있습니다.");
        }
        if (!StringUtils.hasText(message.getRoomId())) {
            throw new IllegalArgumentException("roomId가 비어 있습니다.");
        }
        if (!StringUtils.hasText(message.getSender())) {
            throw new IllegalArgumentException("sender가 비어 있습니다.");
        }
        if (requireContent && !StringUtils.hasText(message.getContent())) {
            throw new IllegalArgumentException("content가 비어 있습니다.");
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleWsException(Exception ex) {
        log.warn("WebSocket error: {}", ex.getMessage());
        return ex.getMessage();
    }
}
