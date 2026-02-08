package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.TodoEntity;
import com.example.architecturestudy.entity.enums.TodoStatus;

import java.time.LocalDateTime;

public record GetTodoRes(
        Long id,
        String title,
        String content,
        TodoStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GetTodoRes from(TodoEntity todo) {
        return new GetTodoRes(
                todo.getId(),
                todo.getTitle(),
                todo.getContent(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
