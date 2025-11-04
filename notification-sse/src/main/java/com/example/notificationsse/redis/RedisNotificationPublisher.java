package com.example.notificationsse.redis;

import com.example.notificationsse.application.usecase.sendnotification.NotificationRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationPublisher {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic notificationTopic;
    
    /**
     * Redis Pub/Sub으로 알림 발행
     * 모든 WAS 서버가 이 메시지를 받게 되고,
     * 각 WAS는 자신에게 연결된 사용자에게만 전송
     */
    public void publish(NotificationRes notification) {
        try {
            redisTemplate.convertAndSend(notificationTopic.getTopic(), notification);
            log.debug("Redis 발행: {} → notificationId={}", 
                notification.receiverUserId(), notification.id());
        } catch (Exception e) {
            log.error("Redis 발행 실패: receiverUserId={}", 
                notification.receiverUserId(), e);
        }
    }
}

