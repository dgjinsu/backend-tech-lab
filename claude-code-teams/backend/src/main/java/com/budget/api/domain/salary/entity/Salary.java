package com.budget.api.domain.salary.entity;

import com.budget.api.domain.user.entity.User;
import com.budget.api.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salaries",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_salary_user_year_month",
        columnNames = {"user_id", "year", "month"}
    ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Salary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(length = 200)
    private String memo;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedExpense> fixedExpenses = new ArrayList<>();

    @Builder
    private Salary(User user, Long totalAmount, Integer year, Integer month, String memo) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.year = year;
        this.month = month;
        this.memo = memo;
    }

    public static Salary create(User user, Long totalAmount, Integer year, Integer month, String memo) {
        return Salary.builder()
                .user(user)
                .totalAmount(totalAmount)
                .year(year)
                .month(month)
                .memo(memo)
                .build();
    }

    public void addFixedExpense(FixedExpense fixedExpense) {
        fixedExpenses.add(fixedExpense);
        fixedExpense.setSalary(this);
    }

    public void update(Long totalAmount, String memo, List<FixedExpense> newFixedExpenses) {
        this.totalAmount = totalAmount;
        this.memo = memo;
        this.fixedExpenses.clear();
        if (newFixedExpenses != null) {
            newFixedExpenses.forEach(this::addFixedExpense);
        }
    }

    public long getFixedExpenseTotal() {
        return fixedExpenses.stream()
                .mapToLong(FixedExpense::getAmount)
                .sum();
    }

    public long getAvailableAmount() {
        return totalAmount - getFixedExpenseTotal();
    }
}
