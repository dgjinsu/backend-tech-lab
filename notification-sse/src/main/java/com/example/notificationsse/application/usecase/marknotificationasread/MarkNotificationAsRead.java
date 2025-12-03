package com.example.notificationsse.application.usecase.marknotificationasread;

import java.util.List;

public interface MarkNotificationAsRead {
    void markAsRead(List<Long> notificationIds);
}

