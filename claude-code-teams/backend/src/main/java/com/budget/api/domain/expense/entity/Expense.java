package com.budget.api.domain.expense.entity;

import com.budget.api.domain.category.entity.Category;
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

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    private String memo;

    @Builder
    private Expense(User user, Category category, Long amount, String description, LocalDate expenseDate, String memo) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.memo = memo;
    }

    public static Expense create(User user, Category category, Long amount, String description, LocalDate expenseDate, String memo) {
        return Expense.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .description(description)
                .expenseDate(expenseDate)
                .memo(memo)
                .build();
    }

    public void update(Category category, Long amount, String description, LocalDate expenseDate, String memo) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.memo = memo;
    }
}
