package com.budget.api.domain.salary.dto;

import com.budget.api.domain.salary.entity.Salary;

import java.time.LocalDateTime;
import java.util.List;

public record SalaryResponse(
        Long id,
        Integer year,
        Integer month,
        Long totalAmount,
        Long fixedExpenseTotal,
        Long availableAmount,
        List<FixedExpenseResponse> fixedExpenses,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SalaryResponse from(Salary salary) {
        List<FixedExpenseResponse> fixedExpenseResponses = salary.getFixedExpenses().stream()
                .map(FixedExpenseResponse::from)
                .toList();

        return new SalaryResponse(
                salary.getId(),
                salary.getYear(),
                salary.getMonth(),
                salary.getTotalAmount(),
                salary.getFixedExpenseTotal(),
                salary.getAvailableAmount(),
                fixedExpenseResponses,
                salary.getMemo(),
                salary.getCreatedAt(),
                salary.getUpdatedAt()
        );
    }
}
