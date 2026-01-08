package com.example.nexussse.application.usecase.sendtouser;

import com.example.nexussse.application.port.SseEmitterPort;
import com.example.nexussse.application.usecase.sendtouser.dto.SendToUserReq;
import com.example.nexussse.dto.SseEvent;
import com.example.nexussse.exception.SseErrorCode;
import com.example.nexussse.exception.SseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendToUserService implements SendToUser {

    private final SseEmitterPort sseEmitterPort;

    @Override
    public void sendToUser(SendToUserReq req) {
        String clientKey = createClientKey(req.serviceId(), req.userId());

        SseEmitter emitter = sseEmitterPort.findByClientKey(clientKey)
                .orElseThrow(() -> new SseException(SseErrorCode.CONNECTION_NOT_FOUND));

        SseEvent event = req.event();

        try {
            emitter.send(SseEmitter.event()
                    .id(event.getId())
                    .name(event.getName())
                    .data(event.getData()));
            log.info("이벤트 전송 완료: {} -> {}", clientKey, event.getName());
        } catch (IOException e) {
            log.error("이벤트 전송 실패: {}, error: {}", clientKey, e.getMessage());
            sseEmitterPort.deleteByClientKey(clientKey);
            throw new SseException(SseErrorCode.SEND_FAILED);
        }
    }

    private String createClientKey(String serviceId, String userId) {
        return serviceId + ":" + userId;
    }
}
