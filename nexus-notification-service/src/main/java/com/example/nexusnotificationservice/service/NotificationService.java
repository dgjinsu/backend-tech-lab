package com.example.nexusnotificationservice.service;

import com.example.nexusnotificationservice.entity.NotificationEntity;
import com.example.nexusnotificationservice.dto.NotificationCreateRequest;
import com.example.nexusnotificationservice.dto.NotificationResponse;
import com.example.nexusnotificationservice.repository.NotificationRepository;
import com.example.nexussse.application.usecase.sendtouser.SendToUser;
import com.example.nexussse.application.usecase.sendtouser.dto.SendToUserReq;
import com.example.nexussse.dto.SseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private static final String SERVICE_ID = "notification";

    private final NotificationRepository notificationRepository;
    private final SendToUser sendToUser;

    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        NotificationEntity notification = NotificationEntity.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        NotificationEntity saved = notificationRepository.save(notification);

        sendSseNotification(saved);

        return NotificationResponse.from(saved);
    }

    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public void markAsRead(Long id) {
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        notification.markAsRead();
    }

    private void sendSseNotification(NotificationEntity notification) {
        try {
            SseEvent event = SseEvent.of("notification", NotificationResponse.from(notification));
            SendToUserReq req = new SendToUserReq(SERVICE_ID, notification.getUserId(), event);
            sendToUser.sendToUser(req);
            log.info("SSE notification sent to user: {}", notification.getUserId());
        } catch (Exception e) {
            log.warn("Failed to send SSE notification to user: {}. User may not be connected.", notification.getUserId());
        }
    }
}
