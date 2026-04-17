package com.budget.api.global.security;

import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * ABAC 평가: Expense 리소스에 대한 액션(READ/WRITE/DELETE)을
 * subject(user.role, user.departmentId)와 resource(expense.ownerId, expense.departmentId)
 * 속성 조합으로 판단한다.
 */
@Component
@RequiredArgsConstructor
public class ExpensePermissionEvaluator implements PermissionEvaluator {

    private final ExpenseRepository expenseRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId,
                                 String targetType, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            return false;
        }
        if (!"Expense".equals(targetType) || targetId == null) {
            return false;
        }

        if (principal.isAdmin()) {
            return true;
        }

        Expense expense = expenseRepository.findById(toLong(targetId)).orElse(null);
        if (expense == null) {
            return false;
        }

        boolean sameDepartment = expense.getDepartment().getId().equals(principal.departmentId());
        boolean isOwner = expense.getUser().getId().equals(principal.userId());
        String action = permission == null ? "" : permission.toString();

        return switch (action) {
            case "READ" -> sameDepartment;
            case "WRITE", "DELETE" -> principal.isManager()
                    ? sameDepartment
                    : (sameDepartment && isOwner);
            default -> false;
        };
    }

    private Long toLong(Serializable id) {
        if (id instanceof Long l) return l;
        if (id instanceof Number n) return n.longValue();
        return Long.parseLong(id.toString());
    }
}
