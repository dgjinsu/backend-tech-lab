package com.budget.api.domain.budget.dto;

import com.budget.api.domain.budget.entity.Budget;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BudgetResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long amount;
    private Integer year;
    private Integer month;

    public static BudgetResponse from(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .amount(budget.getAmount())
                .year(budget.getYear())
                .month(budget.getMonth())
                .build();
    }
}
