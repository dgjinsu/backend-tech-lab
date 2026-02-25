package com.budget.api.domain.expense.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ExpenseCreateRequest {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotNull(message = "지출 금액은 필수입니다.")
    @Min(value = 1, message = "지출 금액은 1 이상이어야 합니다.")
    private Long amount;

    @NotNull(message = "지출 날짜는 필수입니다.")
    private LocalDate date;

    private String memo;
}
