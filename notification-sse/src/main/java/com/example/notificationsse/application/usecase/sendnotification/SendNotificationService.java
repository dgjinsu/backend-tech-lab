package com.example.notificationsse.application.usecase.sendnotification;

import com.example.notificationsse.entity.Notification;
import com.example.notificationsse.entity.User;
import com.example.notificationsse.repository.NotificationRepository;
import com.example.notificationsse.repository.UserRepository;
import com.example.notificationsse.service.SseEmitterService;
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
    private final SseEmitterService sseEmitterService;
    
    @Override
    public NotificationRes sendNotification(SendNotificationReq req) {
        User sender = userRepository.findByUserId(req.senderUserId())
            .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다: " + req.senderUserId()));
        
        User receiver = userRepository.findByUserId(req.receiverUserId())
            .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다: " + req.receiverUserId()));
        
        Notification notification = new Notification(sender, receiver, req.message());
        notificationRepository.save(notification);
        
        // SSE를 통해 실시간으로 알림 전송 시도
        NotificationRes notificationRes = NotificationRes.from(notification);
        boolean sent = sseEmitterService.sendToUser(receiver.getUserId(), notificationRes);
        
        // 실시간 전송 성공 시 즉시 읽음 처리
        if (sent) {
            notification.markAsRead();
            log.info("실시간 알림 전송 성공 및 읽음 처리: notificationId={}", notification.getId());
        } else {
            log.info("수신자 미연결. 나중에 전송될 예정: notificationId={}", notification.getId());
        }
        
        return notificationRes;
    }
}


