package com.example.todoappserver.todo.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "todo-001", "TODO 정보가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String errorMessage;
}
