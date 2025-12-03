package com.example.notificationsse.controller;

import com.example.notificationsse.application.usecase.sendnotification.NotificationRes;
import com.example.notificationsse.application.usecase.sendnotification.SendNotification;
import com.example.notificationsse.application.usecase.sendnotification.SendNotificationReq;
import com.example.notificationsse.service.SseEmitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final SendNotification sendNotification;
    private final SseEmitterService sseEmitterService;
    
    @Operation(summary = "SSE 연결", description = "특정 사용자의 SSE 연결을 생성합니다.")
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String userId) {
        return sseEmitterService.createEmitter(userId);
    }
    
    @Operation(summary = "알림 전송", description = "특정 사용자에게 알림을 전송합니다.")
    @PostMapping
    public ResponseEntity<NotificationRes> send(@RequestBody SendNotificationReq req) {
        return ResponseEntity.ok(sendNotification.sendNotification(req));
    }
}

