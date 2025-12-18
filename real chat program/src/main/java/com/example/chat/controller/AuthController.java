package com.example.chat.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.getOrDefault("username", "").trim();
        if (username.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "username is required"));
        session.setAttribute("username", username);
        return ResponseEntity.ok(Map.of("username", username));
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object username = session.getAttribute("username");
        return ResponseEntity.ok(Map.of("username", username == null ? "" : username.toString()));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
