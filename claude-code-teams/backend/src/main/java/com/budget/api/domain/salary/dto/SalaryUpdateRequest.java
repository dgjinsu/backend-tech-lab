package com.budget.api.domain.salary.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SalaryUpdateRequest(
        @NotNull(message = "총 급여액은 필수입니다.")
        @Min(value = 0, message = "총 급여액은 0 이상이어야 합니다.")
        Long totalAmount,

        @Valid
        List<FixedExpenseRequest> fixedExpenses,

        @Size(max = 200, message = "메모는 200자 이하여야 합니다.")
        String memo
) {
}
