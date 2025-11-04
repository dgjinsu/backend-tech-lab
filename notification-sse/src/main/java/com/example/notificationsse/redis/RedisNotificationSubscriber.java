package com.example.notificationsse.redis;

import com.example.notificationsse.application.usecase.marknotificationasread.MarkNotificationAsRead;
import com.example.notificationsse.application.usecase.sendnotification.NotificationRes;
import com.example.notificationsse.service.SseEmitterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationSubscriber implements MessageListener {
    
    private final SseEmitterService sseEmitterService;
    private final MarkNotificationAsRead markNotificationAsRead;
    private final ObjectMapper objectMapper;
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            NotificationRes notification = objectMapper.readValue(body, NotificationRes.class);
            
            // 모든 WAS가 이 메서드를 호출하지만, 실제 전송은 해당 사용자가 연결된 WAS에서만 성공
            boolean sent = sseEmitterService.sendToUserLocal(
                notification.receiverUserId(), notification);
            
            if (sent) {
                // 이 WAS에 연결된 사용자였음 - 읽음 처리
                markNotificationAsRead.markAsRead(List.of(notification.id()));
            }
            
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
}

