package com.example.nexussse.application.usecase.sendtouser.dto;

import com.example.nexussse.dto.SseEvent;

public record SendToUserReq(String serviceId, String userId, SseEvent event) {
}
