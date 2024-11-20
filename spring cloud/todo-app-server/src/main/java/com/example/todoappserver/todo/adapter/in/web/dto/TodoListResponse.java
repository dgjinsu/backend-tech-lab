package com.example.todoappserver.todo.adapter.in.web.dto;

import com.example.todoappserver.todo.adapter.out.feign.dto.MemberInfoResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record TodoListResponse(
        List<TodoResponse> todoResponseList,
        Integer totalTodoNum,
        Integer completedTodoNum,
        MemberInfoResponse memberInfoResponse
) {
}
