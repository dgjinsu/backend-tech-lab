package com.budget.api.domain.expense.dto;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.expense.entity.Expense;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        CategoryInfo category,
        Long amount,
        String description,
        LocalDate expenseDate,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record CategoryInfo(Long id, String name, String color, String icon) {}

    public static ExpenseResponse from(Expense expense) {
        Category cat = expense.getCategory();
        return new ExpenseResponse(
                expense.getId(),
                new CategoryInfo(cat.getId(), cat.getName(), null, null),
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getMemo(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
