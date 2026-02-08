package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.enums.TodoStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GetTodoRes(
    List<Todo> todos
) {

    public record Todo(
        Long id,
        String title,
        String content,
        TodoStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        public static Todo from(com.example.architecturestudy.entity.TodoEntity todo) {
            return new Todo(
                todo.getId(),
                todo.getTitle(),
                todo.getContent(),
                todo.getStatus(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
            );
        }
    }
}
