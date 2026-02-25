package com.budget.api.domain.expense.dto;

import com.budget.api.domain.expense.entity.Expense;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExpenseResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long amount;
    private LocalDate date;
    private String memo;

    public static ExpenseResponse from(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .memo(expense.getMemo())
                .build();
    }
}
