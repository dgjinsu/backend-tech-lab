package com.example.nexusnotificationservice.dto;

import com.example.nexusnotificationservice.entity.NotificationEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String userId;
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(NotificationEntity notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
