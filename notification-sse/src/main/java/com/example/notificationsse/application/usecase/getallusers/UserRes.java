package com.example.notificationsse.application.usecase.getallusers;

import com.example.notificationsse.entity.User;

public record UserRes(
    Long id,
    String userId,
    String name
) {
    public static UserRes from(User user) {
        return new UserRes(user.getId(), user.getUserId(), user.getName());
    }
}


