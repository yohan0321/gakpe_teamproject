package com.example.chat.model;

public class ChatMessage {

    public enum Type {
        ENTER, TALK, LEAVE, TYPING
    }

    private String roomId;
    private String sender;
    private String content;
    private Type type;
    private Long timestamp;

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
