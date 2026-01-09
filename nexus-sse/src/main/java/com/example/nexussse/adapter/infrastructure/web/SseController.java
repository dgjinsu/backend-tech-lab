package com.example.nexussse.adapter.infrastructure.web;

import com.example.nexussse.application.usecase.sendtouser.SendToUser;
import com.example.nexussse.application.usecase.sendtouser.dto.SendToUserReq;
import com.example.nexussse.application.usecase.subscribe.Subscribe;
import com.example.nexussse.application.usecase.subscribe.dto.SubscribeReq;
import com.example.nexussse.dto.NotificationReq;
import com.example.nexussse.dto.SseEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@Slf4j
public class SseController {

    private final Subscribe subscribe;
    private final SendToUser sendToUser;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestParam String serviceId,
            @RequestParam String userId) {
        log.info("SSE 구독 요청: serviceId={}, userId={}", serviceId, userId);
        return subscribe.subscribe(new SubscribeReq(serviceId, userId)).emitter();
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationReq request) {
        log.info("알림 발송 요청: serviceId={}, userId={}", request.getServiceId(), request.getUserId());
        SseEvent event = SseEvent.of(request.getEventName(), request.getData());
        sendToUser.sendToUser(new SendToUserReq(request.getServiceId(), request.getUserId(), event));
        return ResponseEntity.ok().build();
    }
}
