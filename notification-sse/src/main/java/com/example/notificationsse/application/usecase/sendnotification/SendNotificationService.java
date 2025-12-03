package com.example.notificationsse.application.usecase.sendnotification;

import com.example.notificationsse.entity.Notification;
import com.example.notificationsse.entity.User;
import com.example.notificationsse.redis.RedisNotificationPublisher;
import com.example.notificationsse.repository.NotificationRepository;
import com.example.notificationsse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SendNotificationService implements SendNotification {
    
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final RedisNotificationPublisher redisNotificationPublisher;
    
    @Override
    public NotificationRes sendNotification(SendNotificationReq req) {
        User sender = userRepository.findByUserId(req.senderUserId())
            .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다: " + req.senderUserId()));
        
        User receiver = userRepository.findByUserId(req.receiverUserId())
            .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다: " + req.receiverUserId()));
        
        Notification notification = new Notification(sender, receiver, req.message());
        notificationRepository.save(notification);
        
        // Redis Pub/Sub을 통해 모든 WAS 서버에 알림 발행
        NotificationRes notificationRes = NotificationRes.from(notification);
        redisNotificationPublisher.publish(notificationRes);
        
        return notificationRes;
    }
}


