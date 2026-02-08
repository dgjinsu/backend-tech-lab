package com.example.architecturestudy.dto;

import com.example.architecturestudy.entity.TodoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveTodoReq(
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    String title,

    @Size(max = 500, message = "내용은 500자 이하여야 합니다.")
    String content
) {
    public TodoEntity toEntity() {
        return TodoEntity
            .builder()
            .title(title)
            .content(content)
            .build();
    }
}
