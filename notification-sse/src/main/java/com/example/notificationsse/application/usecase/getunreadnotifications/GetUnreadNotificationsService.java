package com.example.notificationsse.application.usecase.getunreadnotifications;

import com.example.notificationsse.entity.User;
import com.example.notificationsse.repository.NotificationRepository;
import com.example.notificationsse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUnreadNotificationsService implements GetUnreadNotifications {
    
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    
    @Override
    public List<UnreadNotificationRes> getUnreadNotifications(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        return notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtAsc(user)
            .stream()
            .map(UnreadNotificationRes::from)
            .toList();
    }
}

