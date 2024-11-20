package com.example.todoappserver.todo.application.service;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoListResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoSaveRequest;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoUpdateRequest;
import com.example.todoappserver.todo.adapter.out.feign.MemberServerClient;
import com.example.todoappserver.todo.adapter.out.feign.dto.MemberInfoResponse;
import com.example.todoappserver.todo.adapter.out.message.KafkaProducer;
import com.example.todoappserver.todo.application.port.in.SaveTodoUseCase;
import com.example.todoappserver.todo.application.port.in.DeleteTodoUseCase;
import com.example.todoappserver.todo.application.port.in.GetTodoQuery;
import com.example.todoappserver.todo.application.port.in.UpdateTodoUseCase;
import com.example.todoappserver.todo.application.port.out.TodoRepositoryPort;
import com.example.todoappserver.todo.application.service.mapper.TodoDomainMapper;
import com.example.todoappserver.todo.domain.Todo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TodoService implements SaveTodoUseCase, DeleteTodoUseCase, GetTodoQuery,
    UpdateTodoUseCase {

    private final TodoRepositoryPort todoRepositoryPort;
    private final TodoDomainMapper todoMapper;
    private final KafkaProducer kafkaProducer;
    private final MemberServerClient memberServerClient;

    @Override
    public TodoResponse saveTodo(Long memberId, TodoSaveRequest request) {
        Todo todo = todoMapper.toDomain(memberId, request);
        Todo savedTodo = todoRepositoryPort.saveTodo(todo);

        TodoResponse response = todoMapper.toResponse(savedTodo);

        log.info("todo 저장");
        // Kafka로 메시지 발행
        kafkaProducer.send("todo-save-topic", response);

        return response;
    }

    @Override
    public void deleteCompletedTodo(Long memberId) {
        int deletedTodoCnt = todoRepositoryPort.deleteCompletedTodo(memberId);
        log.info("제거된 TODO 수: {}", deletedTodoCnt);
    }

    @Override
    public void deleteTodoById(Long todoId) {
        todoRepositoryPort.deleteById(todoId);
    }

    @Override
    public TodoListResponse getTodoList(Long memberId) {
        List<Todo> todoList = todoRepositoryPort.findAllByMemberId(memberId);

        // feign client 로 member 정보 불러오기)
        MemberInfoResponse memberInfo = memberServerClient.getMemberInfo();

        return todoMapper.toResponseList(todoList, memberInfo);
    }

    @Override
    public TodoResponse updateTodo(Long todoId, TodoUpdateRequest request) {
        Todo todo = todoRepositoryPort.findById(todoId);

        todo.update(request.content(), request.isComplete());

        Todo saveTodo = todoRepositoryPort.saveTodo(todo);

        return todoMapper.toResponse(saveTodo);
    }
}
