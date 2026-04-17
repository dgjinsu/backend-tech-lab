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

// [ABAC 리소스 속성 집합] ownerId/departmentId/amount/status 네 필드가 전부 권한 판정에 쓰인다.
// - ownerId: 본인 소유인지 (canEditDraft)
// - departmentId: 승인자와 같은 부서인지 (sameDept)
// - amount: MANAGER 승인 한도 판정
// - status: 상태 게이트(어떤 액션이 허용되는지)
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

    // BigDecimal + precision/scale: 금액 반올림 오차 방지를 위해 double 금지.
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExpenseStatus status;

    // 신규 생성은 항상 DRAFT. 이 규칙이 깨지면 submit 전 상태 판정이 어긋난다.
    public Expense(Long ownerId, Long departmentId, BigDecimal amount, String description) {
        this.ownerId = ownerId;
        this.departmentId = departmentId;
        this.amount = amount;
        this.description = description;
        this.status = ExpenseStatus.DRAFT;
    }

    // DRAFT에서만 부를 것. DRAFT 체크는 정책·서비스 레이어 책임이라 엔티티는 값만 갈아끼움.
    public void updateDraft(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    // 상태 전이 자체의 유효성(DRAFT→SUBMITTED만 허용 등)은 Service의 assertStatus가 담당.
    public void changeStatus(ExpenseStatus next) {
        this.status = next;
    }

    public boolean isDraft() {
        return status == ExpenseStatus.DRAFT;
    }
}
