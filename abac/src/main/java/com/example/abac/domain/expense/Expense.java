package com.example.abac.domain.expense;

import com.example.abac.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "expenses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status;

    public Expense(Long ownerId, Long departmentId, BigDecimal amount, String description) {
        this.ownerId = ownerId;
        this.departmentId = departmentId;
        this.amount = amount;
        this.description = description;
        this.status = ExpenseStatus.DRAFT;
    }

    public void updateDraft(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public void changeStatus(ExpenseStatus next) {
        this.status = next;
    }

    public boolean isDraft() {
        return status == ExpenseStatus.DRAFT;
    }
}
