package com.budget.api.domain.salary.dto;

import com.budget.api.domain.salary.entity.Salary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryResponse {

    private Long id;
    private Long amount;
    private Integer year;
    private Integer month;
    private Long fixedExpense;
    private String memo;

    public static SalaryResponse from(Salary salary) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .amount(salary.getAmount())
                .year(salary.getYear())
                .month(salary.getMonth())
                .fixedExpense(salary.getFixedExpense())
                .memo(salary.getMemo())
                .build();
    }
}
