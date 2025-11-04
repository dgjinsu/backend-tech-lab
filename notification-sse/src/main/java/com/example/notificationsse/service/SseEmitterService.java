package com.example.notificationsse.service;

import com.example.notificationsse.application.usecase.getunreadnotifications.GetUnreadNotifications;
import com.example.notificationsse.application.usecase.getunreadnotifications.UnreadNotificationRes;
import com.example.notificationsse.application.usecase.marknotificationasread.MarkNotificationAsRead;
import com.example.notificationsse.application.usecase.sendnotification.NotificationRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {
    
    private final GetUnreadNotifications getUnreadNotifications;
    private final MarkNotificationAsRead markNotificationAsRead;
    
    // userId를 키로 SseEmitter를 관리
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    // SSE 연결 생성 (타임아웃: 1시간)
    public SseEmitter createEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(3600000L); // 1시간
        emitters.put(userId, emitter);
        
        // 연결 완료 시 emitter 제거
        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료: userId={}", userId);
            emitters.remove(userId);
        });
        
        // 타임아웃 시 emitter 제거
        emitter.onTimeout(() -> {
            log.info("SSE 타임아웃: userId={}", userId);
            emitter.complete();
            emitters.remove(userId);
        });
        
        // 에러 발생 시 emitter 제거
        emitter.onError((e) -> {
            log.error("SSE 에러: userId={}, error={}", userId, e.getMessage());
            emitters.remove(userId);
        });
        
        // 연결 직후 더미 이벤트 전송 (일부 프록시는 첫 응답이 있어야 연결을 유지함)
        try {
            emitter.send(SseEmitter.event()
                .name("connect")
                .data("연결되었습니다."));
            log.info("SSE 연결 생성: userId={}", userId);
            
            // 미확인 알림 전송
            sendUnreadNotifications(userId, emitter);
            
        } catch (IOException e) {
            log.error("SSE 더미 이벤트 전송 실패: userId={}", userId, e);
            emitters.remove(userId);
        }
        
        return emitter;
    }
    
    // 미확인 알림 전송
    private void sendUnreadNotifications(String userId, SseEmitter emitter) {
        try {
            List<UnreadNotificationRes> unreadNotifications = getUnreadNotifications.getUnreadNotifications(userId);
            
            if (!unreadNotifications.isEmpty()) {
                log.info("미확인 알림 전송 시작: userId={}, count={}", userId, unreadNotifications.size());
                
                List<Long> notificationIds = unreadNotifications.stream()
                    .map(UnreadNotificationRes::id)
                    .toList();
                
                // 각 미확인 알림을 순차적으로 전송
                for (UnreadNotificationRes notification : unreadNotifications) {
                    try {
                        Thread.sleep(100); // 알림 간 간격 (100ms)
                        emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                        log.info("미확인 알림 전송: notificationId={}", notification.id());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("알림 전송 중단: userId={}", userId);
                        break;
                    }
                }
                
                // 전송 완료 후 읽음 처리
                markNotificationAsRead.markAsRead(notificationIds);
                log.info("미확인 알림 읽음 처리 완료: userId={}, count={}", userId, notificationIds.size());
            }
        } catch (Exception e) {
            log.error("미확인 알림 전송 실패: userId={}", userId, e);
        }
    }
    
    // 특정 사용자에게 알림 전송 (성공 여부 반환)
    public boolean sendToUser(String userId, NotificationRes notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
                log.info("알림 전송 성공: userId={}, notification={}", userId, notification);
                return true;
            } catch (IOException e) {
                log.error("알림 전송 실패: userId={}", userId, e);
                emitters.remove(userId);
                return false;
            }
        } else {
            log.warn("연결된 emitter가 없습니다. 나중에 전송됩니다: userId={}", userId);
            return false;
        }
    }
}