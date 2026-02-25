package com.budget.api.domain.salary.dto;

import com.budget.api.domain.salary.entity.Salary;

public record SalaryListResponse(
        Long id,
        Integer year,
        Integer month,
        Long totalAmount,
        Long fixedExpenseTotal,
        Long availableAmount,
        String memo
) {
    public static SalaryListResponse from(Salary salary) {
        return new SalaryListResponse(
                salary.getId(),
                salary.getYear(),
                salary.getMonth(),
                salary.getTotalAmount(),
                salary.getFixedExpenseTotal(),
                salary.getAvailableAmount(),
                salary.getMemo()
        );
    }
}
