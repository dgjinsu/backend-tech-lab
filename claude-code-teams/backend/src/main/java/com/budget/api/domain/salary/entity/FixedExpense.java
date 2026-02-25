package com.budget.api.domain.salary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fixed_expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public FixedExpense(String name, Long amount) {
        this.name = name;
        this.amount = amount;
    }

    void setSalary(Salary salary) {
        this.salary = salary;
    }
}
