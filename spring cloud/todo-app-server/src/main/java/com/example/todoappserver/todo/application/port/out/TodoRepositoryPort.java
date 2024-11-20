package com.example.todoappserver.todo.application.port.out;

import com.example.todoappserver.todo.domain.Todo;
import java.util.List;

public interface TodoRepositoryPort {
    Todo saveTodo(Todo todo);

    int deleteCompletedTodo(Long memberId);

    void deleteById(Long todoId);

    List<Todo> findAllByMemberId(Long memberId);

    Todo findById(Long todoId);
}
