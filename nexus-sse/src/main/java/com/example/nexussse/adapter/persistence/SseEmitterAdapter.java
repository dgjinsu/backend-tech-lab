package com.example.nexussse.adapter.persistence;

import com.example.nexussse.application.port.SseEmitterPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SseEmitterAdapter implements SseEmitterPort {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(String clientKey, SseEmitter emitter) {
        SseEmitter oldEmitter = emitters.put(clientKey, emitter);
        if (oldEmitter != null) {
            oldEmitter.complete();
            log.info("기존 연결 종료 후 새 연결로 교체: {}", clientKey);
        }
    }

    @Override
    public Optional<SseEmitter> findByClientKey(String clientKey) {
        return Optional.ofNullable(emitters.get(clientKey));
    }

    @Override
    public void deleteByClientKey(String clientKey) {
        emitters.remove(clientKey);
        log.info("연결 제거됨: {}", clientKey);
    }
}
