package com.budget.api.domain.budget.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetCreateRequest {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotNull(message = "예산 금액은 필수입니다.")
    @Min(value = 0, message = "예산 금액은 0 이상이어야 합니다.")
    private Long amount;

    @NotNull(message = "연도는 필수입니다.")
    private Integer year;

    @NotNull(message = "월은 필수입니다.")
    @Min(value = 1, message = "월은 1 이상이어야 합니다.")
    @Max(value = 12, message = "월은 12 이하여야 합니다.")
    private Integer month;
}
