package com.budget.api.domain.expense.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExpenseUpdateRequest(
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId,

        @NotNull(message = "지출 금액은 필수입니다.")
        @Min(value = 1, message = "지출 금액은 1 이상이어야 합니다.")
        Long amount,

        @NotBlank(message = "지출 내용은 필수입니다.")
        @Size(max = 100, message = "지출 내용은 100자 이내여야 합니다.")
        String description,

        @NotNull(message = "지출 날짜는 필수입니다.")
        LocalDate expenseDate,

        @Size(max = 200, message = "메모는 200자 이내여야 합니다.")
        String memo
) {}
