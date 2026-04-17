package com.example.abac.domain.expense;

import com.example.abac.security.CustomUserPrincipal;
import org.springframework.data.jpa.domain.Specification;

public final class ExpenseSpecifications {

    private ExpenseSpecifications() {
    }

    public static Specification<Expense> visibleTo(CustomUserPrincipal principal) {
        return switch (principal.getRole()) {
            case ADMIN -> (root, query, cb) -> cb.conjunction();
            case FINANCE -> (root, query, cb) -> root.get("status")
                    .in(ExpenseStatus.APPROVED, ExpenseStatus.PAID);
            case MANAGER -> (root, query, cb) -> cb.equal(
                    root.get("departmentId"), principal.getDepartmentId());
            case EMPLOYEE -> (root, query, cb) -> cb.equal(
                    root.get("ownerId"), principal.getUserId());
        };
    }

    public static Specification<Expense> hasId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
