package com.budget.api.domain.salary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FixedExpenseRequest(
        @NotBlank(message = "고정 지출 항목명은 필수입니다.")
        @Size(max = 50, message = "고정 지출 항목명은 50자 이하여야 합니다.")
        String name,

        @NotNull(message = "고정 지출 금액은 필수입니다.")
        @Min(value = 0, message = "고정 지출 금액은 0 이상이어야 합니다.")
        Long amount
) {
}
