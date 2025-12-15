package com.example.chat.controller;

import com.example.chat.service.RoomUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class RoomRestController {

    private final RoomUserService roomUserService;

    public RoomRestController(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }

    @GetMapping("/api/rooms/{roomId}/users")
    public List<String> getRoomUsers(@PathVariable("roomId") String roomId) {
        List<String> users = new ArrayList<>(roomUserService.getUsers(roomId));
        Collections.sort(users);
        return users;
    }

}
