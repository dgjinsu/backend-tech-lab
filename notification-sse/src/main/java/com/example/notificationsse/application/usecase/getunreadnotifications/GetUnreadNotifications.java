package com.example.notificationsse.application.usecase.getunreadnotifications;

import java.util.List;

public interface GetUnreadNotifications {
    List<UnreadNotificationRes> getUnreadNotifications(String userId);
}

