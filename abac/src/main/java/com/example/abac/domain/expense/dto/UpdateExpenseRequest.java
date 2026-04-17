package com.example.abac.domain.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// 수정 허용 여부(DRAFT 상태 + 본인 소유)는 ExpensePolicy.canEditDraft에서 판정하므로
// DTO는 값 유효성만 책임진다. status/ownerId 같은 '권한에 영향을 주는 필드'는 받지 않는다.
public record UpdateExpenseRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description
) {
}
