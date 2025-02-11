package com.mirero.pwm.sse.service;
import com.mirero.pwm.sse.repository.EmitterRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SseService {

    private static final long TIMEOUT = 30 * 1000L;

    private final EmitterRepository emitterRepository;

    public SseService(EmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    // SSE 구독
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        emitter.onCompletion(() -> emitterRepository.removeClient(userId));
        emitter.onTimeout(() -> emitterRepository.removeClient(userId));

        emitterRepository.addClient(userId, emitter);

        return emitter;
    }

    // 현재 구독 중인 사용자 목록 조회
    public List<String> getSubscribedUsers() {
        return emitterRepository.getAllUsers();
    }

    // 특정 사용자에게 알람 전송
    public void sendNotification(String userId, String message) {
        Optional<SseEmitter> optionalEmitter = emitterRepository.getClient(userId);
        optionalEmitter.ifPresent(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                emitterRepository.removeClient(userId);
            }
        });
    }
}