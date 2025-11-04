package com.example.notificationsse.application.usecase.marknotificationasread;

import com.example.notificationsse.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkNotificationAsReadService implements MarkNotificationAsRead {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public void markAsRead(List<Long> notificationIds) {
        notificationIds.forEach(id -> {
            notificationRepository.findById(id).ifPresent(notification -> {
                notification.markAsRead();
                log.info("알림 읽음 처리: notificationId={}", id);
            });
        });
    }
}

