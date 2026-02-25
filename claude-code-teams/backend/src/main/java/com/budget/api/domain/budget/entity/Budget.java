package com.budget.api.domain.budget.entity;

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

@Entity
@Table(name = "budgets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Builder
    private Budget(User user, Category category, Long amount, Integer year, Integer month) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.year = year;
        this.month = month;
    }

    public static Budget create(User user, Category category, Long amount, Integer year, Integer month) {
        return Budget.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .year(year)
                .month(month)
                .build();
    }

    public void updateAmount(Long amount) {
        this.amount = amount;
    }
}
