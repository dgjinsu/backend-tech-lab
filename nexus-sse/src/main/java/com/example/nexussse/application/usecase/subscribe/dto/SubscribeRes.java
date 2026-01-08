package com.example.nexussse.application.usecase.subscribe.dto;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public record SubscribeRes(
    SseEmitter emitter
) {
}
