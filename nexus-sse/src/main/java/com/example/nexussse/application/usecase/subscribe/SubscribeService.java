package com.example.nexussse.application.usecase.subscribe;

import com.example.nexussse.application.port.SseEmitterPort;
import com.example.nexussse.application.usecase.subscribe.dto.SubscribeReq;
import com.example.nexussse.application.usecase.subscribe.dto.SubscribeRes;
import com.example.nexussse.global.config.SseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeService implements Subscribe {

    private final SseEmitterPort sseEmitterPort;
    private final SseConfig sseConfig;

    @Override
    public SubscribeRes subscribe(SubscribeReq req) {
        String clientKey = createClientKey(req.serviceId(), req.userId());

        SseEmitter emitter = new SseEmitter(sseConfig.getTimeout());

        registerEmitterCallbacks(emitter, clientKey);

        sseEmitterPort.save(clientKey, emitter);

        sendInitEvent(emitter, clientKey);

        log.info("SSE 구독 완료: {}", clientKey);
        return new SubscribeRes(emitter);
    }

    private String createClientKey(String serviceId, String userId) {
        return serviceId + ":" + userId;
    }

    private void sendInitEvent(SseEmitter emitter, String clientKey) {
        try {
            emitter.send(SseEmitter.event()
                    .id(clientKey)
                    .name("connect")
                    .data("connected")
                    .reconnectTime(sseConfig.getReconnectTime()));
        } catch (IOException e) {
            log.error("초기 이벤트 전송 실패: {}", clientKey);
            sseEmitterPort.deleteByClientKey(clientKey);
        }
    }

    private void registerEmitterCallbacks(SseEmitter emitter, String clientKey) {
        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료: {}", clientKey);
            sseEmitterPort.deleteByClientKey(clientKey);
        });

        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃: {}", clientKey);
            emitter.complete();
        });

        emitter.onError(e -> {
            log.error("SSE 연결 에러: {}, error: {}", clientKey, e.getMessage());
            sseEmitterPort.deleteByClientKey(clientKey);
        });
    }
}
