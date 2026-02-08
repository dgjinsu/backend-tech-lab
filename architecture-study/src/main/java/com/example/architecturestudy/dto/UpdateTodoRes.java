package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.TodoEntity;

public record UpdateTodoRes(
        Long id,
        String title,
        String content
) {
    public static UpdateTodoRes from(TodoEntity todo) {
        return new UpdateTodoRes(
                todo.getId(),
                todo.getTitle(),
                todo.getContent()
        );
    }
}
