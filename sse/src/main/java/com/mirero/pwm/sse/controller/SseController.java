package com.mirero.pwm.sse.controller;

import com.mirero.pwm.sse.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    // 클라이언트가 SSE 구독
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String userId) {
        return sseService.subscribe(userId);
    }

    // 현재 구독 중인 사용자 목록 조회
    @GetMapping("/users")
    public List<String> getUsers() {
        return sseService.getSubscribedUsers();
    }

    // 특정 사용자에게 알람 전송
    @PostMapping("/send/{userId}")
    public void sendNotification(@PathVariable String userId, @RequestParam String message) {
        sseService.sendNotification(userId, message);
    }
}
