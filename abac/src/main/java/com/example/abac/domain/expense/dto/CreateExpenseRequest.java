package com.example.abac.domain.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// 의도적으로 ownerId/departmentId를 받지 않는다. 이 두 값은 '클라이언트가 보낸 값'을
// 믿으면 안 되고, SecurityContext의 Principal에서만 꺼내 쓴다(Service 단에서 주입).
// → ABAC의 '주체 속성'이 요청 바디로 위조되지 않도록 차단.
public record CreateExpenseRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description
) {
}
