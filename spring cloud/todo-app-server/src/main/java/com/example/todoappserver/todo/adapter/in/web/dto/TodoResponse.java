package com.example.todoappserver.todo.adapter.in.web.dto;

import lombok.Builder;

@Builder
public record TodoResponse(
        Long todoId,
        String content,
        Boolean isCompleted,
        String createdAt
) {
}
