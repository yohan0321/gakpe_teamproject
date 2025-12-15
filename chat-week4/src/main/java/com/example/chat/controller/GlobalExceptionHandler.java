package com.example.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 프론트에서는 "HTTP 500"만 보이기 때문에, 실제 원인을 빠르게 확인할 수 있도록
 * JSON 형태로 에러 메시지를 내려주는 전역 예외 처리기.
 * (배포 시엔 에러 상세 노출을 줄이는 것을 권장)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException e) {
        log.warn("400 Bad Request: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload("BAD_REQUEST", e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleServerError(Exception e) {
        log.error("500 Server Error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload("INTERNAL_ERROR", e));
    }

    private Map<String, Object> payload(String code, Exception e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", e.getMessage());
        body.put("exception", e.getClass().getName());
        return body;
    }
}
