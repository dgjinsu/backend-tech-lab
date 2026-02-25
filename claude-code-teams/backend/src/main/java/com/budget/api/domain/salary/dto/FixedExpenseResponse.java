package com.budget.api.domain.salary.dto;

import com.budget.api.domain.salary.entity.FixedExpense;

public record FixedExpenseResponse(
        Long id,
        String name,
        Long amount
) {
    public static FixedExpenseResponse from(FixedExpense fixedExpense) {
        return new FixedExpenseResponse(
                fixedExpense.getId(),
                fixedExpense.getName(),
                fixedExpense.getAmount()
        );
    }
}
