package com.example.tempserver.domain.temp.message.dto;

import lombok.Builder;

@Builder
public record TodoResponseDto(
        Long todoId,
        String content,
        Boolean isCompleted
) {
}
