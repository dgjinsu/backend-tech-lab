package com.example.todoappserver.todo.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TodoSaveRequest(
        @Schema(description = "TODO 내용", example = "양치하기")
        String content
) {
}
