package com.example.notificationsse.application.usecase.sendnotification;

import io.swagger.v3.oas.annotations.media.Schema;

public record SendNotificationReq(
    @Schema(description = "발신자 사용자 ID", example = "user1")
    String senderUserId,
    @Schema(description = "수신자 사용자 ID", example = "user2")
    String receiverUserId,
    @Schema(description = "알림 메시지", example = "안녕하세요!")
    String message
) {
}


