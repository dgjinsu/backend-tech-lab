package com.example.todoappserver.todo.application.port.in;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoUpdateRequest;

public interface UpdateTodoUseCase {

    TodoResponse updateTodo(Long todoId, TodoUpdateRequest request);
}
