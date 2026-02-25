package com.budget.api.domain.salary.entity;

import com.budget.api.domain.user.entity.User;
import com.budget.api.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Salary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Long fixedExpense;

    private String memo;

    @Builder
    private Salary(User user, Long amount, Integer year, Integer month, Long fixedExpense, String memo) {
        this.user = user;
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.fixedExpense = fixedExpense != null ? fixedExpense : 0L;
        this.memo = memo;
    }

    public static Salary create(User user, Long amount, Integer year, Integer month, Long fixedExpense, String memo) {
        return Salary.builder()
                .user(user)
                .amount(amount)
                .year(year)
                .month(month)
                .fixedExpense(fixedExpense)
                .memo(memo)
                .build();
    }

    public void update(Long amount, Long fixedExpense, String memo) {
        this.amount = amount;
        this.fixedExpense = fixedExpense != null ? fixedExpense : this.fixedExpense;
        this.memo = memo;
    }
}
