package com.example.abac.domain.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateExpenseRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description
) {
}
