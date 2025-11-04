package com.example.notificationsse.repository;

import com.example.notificationsse.entity.Notification;
import com.example.notificationsse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
    
    // 읽지 않은 알림 조회 (생성 시간 오름차순)
    List<Notification> findByReceiverAndIsReadFalseOrderByCreatedAtAsc(User receiver);
}

