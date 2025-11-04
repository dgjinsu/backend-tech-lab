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
        emitter.onCompletion(() -> emitters.remove(userId));
        
        // 타임아웃 시 emitter 제거
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(userId);
        });
        
        // 에러 발생 시 emitter 제거
        emitter.onError((e) -> emitters.remove(userId));
        
        // 연결 직후 더미 이벤트 전송
        try {
            emitter.send(SseEmitter.event()
                .name("connect")
                .data("연결되었습니다."));
            log.info("SSE 연결: userId={}", userId);
            
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
                log.info("미확인 알림 {}개 전송 시작: userId={}", unreadNotifications.size(), userId);
                
                List<Long> notificationIds = unreadNotifications.stream()
                    .map(UnreadNotificationRes::id)
                    .toList();
                
                // 각 미확인 알림을 순차적으로 전송
                for (UnreadNotificationRes notification : unreadNotifications) {
                    try {
                        Thread.sleep(100);
                        emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // 전송 완료 후 읽음 처리
                markNotificationAsRead.markAsRead(notificationIds);
            }
        } catch (Exception e) {
            log.error("미확인 알림 전송 실패: userId={}", userId, e);
        }
    }
    
    /**
     * Redis Subscriber에서 호출되는 메서드
     * 모든 WAS가 동일한 Redis 메시지를 받지만,
     * 각 WAS는 자신에게 연결된 사용자에게만 전송
     */
    public boolean sendToUserLocal(String userId, NotificationRes notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
                log.info("알림 전송: {} → notificationId={}", userId, notification.id());
                return true;
            } catch (IOException e) {
                log.error("알림 전송 실패: userId={}", userId, e);
                emitters.remove(userId);
                return false;
            }
        }
        return false;
    }
}