package com.example.notificationsse.application.usecase.getunreadnotifications;

import com.example.notificationsse.entity.Notification;

import java.time.LocalDateTime;

public record UnreadNotificationRes(
    Long id,
    String senderUserId,
    String senderName,
    String receiverUserId,
    String receiverName,
    String message,
    LocalDateTime createdAt,
    Boolean isRead
) {
    public static UnreadNotificationRes from(Notification notification) {
        return new UnreadNotificationRes(
            notification.getId(),
            notification.getSender().getUserId(),
            notification.getSender().getName(),
            notification.getReceiver().getUserId(),
            notification.getReceiver().getName(),
            notification.getMessage(),
            notification.getCreatedAt(),
            notification.getIsRead()
        );
    }
}

