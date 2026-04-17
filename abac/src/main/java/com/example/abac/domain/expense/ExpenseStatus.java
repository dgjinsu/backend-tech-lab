package com.example.abac.domain.expense;

// 상태 머신. 허용되는 전이는 ExpenseService에서 assertStatus()로 강제한다.
// 허용 전이:
//   DRAFT     → SUBMITTED (submit, 본인만)
//   SUBMITTED → APPROVED  (approve, 같은 부서 MANAGER/ADMIN)
//   SUBMITTED → REJECTED  (reject, 동일 조건)
//   APPROVED  → PAID      (pay, FINANCE/ADMIN)
public enum ExpenseStatus {
    DRAFT,      // 초안
    SUBMITTED,  // 제출됨
    APPROVED,   // 승인됨
    REJECTED,   // 반려됨
    PAID        // 지급됨
}
