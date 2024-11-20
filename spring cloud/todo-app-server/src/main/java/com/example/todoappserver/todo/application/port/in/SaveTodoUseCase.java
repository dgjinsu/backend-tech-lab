package com.example.todoappserver.todo.application.port.in;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoSaveRequest;

public interface SaveTodoUseCase {

    TodoResponse saveTodo(Long memberId, TodoSaveRequest request);
}
