package com.example.todoappserver.todo.adapter.out.persistence;

import com.example.todoappserver.common.annotation.PersistenceAdapter;
import com.example.todoappserver.todo.infrastructure.exception.ErrorCode;
import com.example.todoappserver.todo.infrastructure.exception.TodoAppException;
import com.example.todoappserver.todo.adapter.out.mapper.TodoEntityMapper;
import com.example.todoappserver.todo.application.port.out.TodoRepositoryPort;
import com.example.todoappserver.todo.domain.Todo;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PersistenceAdapter
public class TodoRepositoryAdaptor implements TodoRepositoryPort {

    private final TodoRepository todoRepository;
    private final TodoEntityMapper todoMapper;

    @Override
    public Todo saveTodo(Todo todo) {
        TodoEntity todoEntity = todoMapper.toEntity(todo);

        TodoEntity saveTodoEntity = todoRepository.save(todoEntity);

        return todoMapper.toDomain(todoEntity);
    }

    @Override
    public int deleteCompletedTodo(Long memberId) {
        return todoRepository.deleteCompletedTodo(memberId);
    }

    @Override
    public void deleteById(Long todoId) {
        todoRepository.deleteById(todoId);
    }

    @Override
    public List<Todo> findAllByMemberId(Long memberId) {
        List<TodoEntity> todoEntityList = todoRepository.findAllByMemberId(memberId);

        return todoMapper.toDomainList(todoEntityList);
    }

    @Override
    public Todo findById(Long todoId) {
        TodoEntity todoEntity = todoRepository.findById(todoId)
            .orElseThrow(() -> new TodoAppException(ErrorCode.TODO_NOT_FOUND));

        return todoMapper.toDomain(todoEntity);
    }
}
