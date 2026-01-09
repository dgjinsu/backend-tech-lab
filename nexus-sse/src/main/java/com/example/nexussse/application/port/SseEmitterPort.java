package com.example.nexussse.application.port;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface SseEmitterPort {

    void save(String clientKey, SseEmitter emitter);

    Optional<SseEmitter> findByClientKey(String clientKey);

    void deleteByClientKey(String clientKey);
}
