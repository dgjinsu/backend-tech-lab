package com.example.todoappserver.todo.application.port.in;

public interface DeleteTodoUseCase {

    void deleteCompletedTodo(Long memberId);

    void deleteTodoById(Long todoId);
}
