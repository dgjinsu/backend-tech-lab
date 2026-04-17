package com.example.abac.domain.expense.dto;

import com.example.abac.domain.expense.Expense;
import com.example.abac.domain.expense.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        Long ownerId,
        Long departmentId,
        BigDecimal amount,
        String description,
        ExpenseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.getId(),
                e.getOwnerId(),
                e.getDepartmentId(),
                e.getAmount(),
                e.getDescription(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
