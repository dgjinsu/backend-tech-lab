package com.example.todoappserver.todo.application.service.mapper;

import com.example.todoappserver.todo.adapter.in.web.dto.TodoListResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoSaveRequest;
import com.example.todoappserver.todo.adapter.out.feign.dto.MemberInfoResponse;
import com.example.todoappserver.todo.domain.Todo;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // bean 으로 관리
public interface TodoDomainMapper {

    TodoDomainMapper INSTANCE = Mappers.getMapper(TodoDomainMapper.class);

    @Mapping(target = "isCompleted", constant = "false")
    Todo toDomain(Long memberId, TodoSaveRequest request);

    @Mapping(source = "id", target = "todoId")
    TodoResponse toResponse(Todo todo);

    default TodoListResponse
    toResponseList(
        List<Todo> todoList, MemberInfoResponse memberInfoResponse) {
        List<TodoResponse> todoResponseList = todoList.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        int totalTodoNum = todoResponseList.size();
        int completedTodoNum = (int) todoResponseList.stream()
            .filter(TodoResponse::isCompleted)
            .count();

        return TodoListResponse.builder()
            .todoResponseList(todoResponseList)
            .totalTodoNum(totalTodoNum)
            .completedTodoNum(completedTodoNum)
            .memberInfoResponse(memberInfoResponse)
            .build();
    }
}
