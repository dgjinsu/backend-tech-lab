package com.example.todoappserver.todo.application.port.in;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoListResponse;

public interface GetTodoQuery {

    TodoListResponse getTodoList(Long memberId);
}
