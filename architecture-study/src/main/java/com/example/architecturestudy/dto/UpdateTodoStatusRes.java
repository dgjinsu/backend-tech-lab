package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.TodoEntity;
import com.example.architecturestudy.entity.enums.TodoStatus;

public record UpdateTodoStatusRes(
        Long id,
        TodoStatus status
) {
    public static UpdateTodoStatusRes from(TodoEntity todo) {
        return new UpdateTodoStatusRes(
                todo.getId(),
                todo.getStatus()
        );
    }
}
