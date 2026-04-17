package com.example.abac.domain.expense.dto;

import com.example.abac.domain.expense.Expense;
import com.example.abac.domain.expense.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 응답에는 ownerId/departmentId/status를 노출해도 OK. 조회 단계에서 이미
// ExpenseSpecifications.visibleTo()가 '볼 수 있는 것만' WHERE 절로 걸러낸 뒤라
// 이 DTO가 본다는 것 자체가 권한 통과를 의미한다.
public record ExpenseResponse(
        Long id,
        Long ownerId,
        Long departmentId,
        BigDecimal amount,
        String description,
        ExpenseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 엔티티 → 응답 DTO 변환을 한 군데로 고정. 컨트롤러에 매핑 로직이 흩어지지 않게.
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.getId(),
                e.getOwnerId(),
                e.getDepartmentId(),
                e.getAmount(),
                e.getDescription(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
