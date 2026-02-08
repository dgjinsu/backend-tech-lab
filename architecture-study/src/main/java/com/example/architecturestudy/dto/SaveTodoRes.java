package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.TodoEntity;
import com.example.architecturestudy.entity.enums.TodoStatus;

public record SaveTodoRes(
    Long id,
    String title,
    TodoStatus status
) {
    public static SaveTodoRes from(TodoEntity todo) {
        return new SaveTodoRes(
            todo.getId(),
            todo.getTitle(),
            todo.getStatus()
        );
    }
}
