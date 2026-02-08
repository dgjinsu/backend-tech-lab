package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.enums.TodoStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTodoStatusReq(
        @NotNull(message = "상태는 필수입니다.")
        TodoStatus status
) {
}
