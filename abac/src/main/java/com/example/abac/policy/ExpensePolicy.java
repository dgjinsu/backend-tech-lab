package com.example.abac.policy;

import com.example.abac.domain.expense.Expense;
import com.example.abac.domain.expense.ExpenseRepository;
import com.example.abac.domain.expense.ExpenseStatus;
import com.example.abac.domain.user.Role;
import com.example.abac.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component("expensePolicy")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpensePolicy {

    public static final BigDecimal MANAGER_APPROVAL_LIMIT = new BigDecimal("1000000");

    private final ExpenseRepository expenseRepository;

    public boolean canEditDraft(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        return expenseRepository.findById(id)
                .map(e -> e.getOwnerId().equals(p.getUserId()) && e.isDraft())
                .orElse(false);
    }

    public boolean canSubmit(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        return expenseRepository.findById(id)
                .map(e -> e.getOwnerId().equals(p.getUserId()) && e.isDraft())
                .orElse(false);
    }

    public boolean canApprove(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        if (p.getRole() != Role.MANAGER) return false;
        return expenseRepository.findById(id)
                .map(e -> sameDept(e, p)
                        && e.getStatus() == ExpenseStatus.SUBMITTED
                        && withinManagerLimit(e))
                .orElse(false);
    }

    public boolean canReject(Long id, Authentication authentication) {
        return canApprove(id, authentication);
    }

    public boolean canPay(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        if (p.getRole() != Role.FINANCE) return false;
        return expenseRepository.findById(id)
                .map(e -> e.getStatus() == ExpenseStatus.APPROVED)
                .orElse(false);
    }

    private static boolean sameDept(Expense e, CustomUserPrincipal p) {
        return e.getDepartmentId().equals(p.getDepartmentId());
    }

    private static boolean withinManagerLimit(Expense e) {
        return e.getAmount().compareTo(MANAGER_APPROVAL_LIMIT) <= 0;
    }

    private static CustomUserPrincipal principal(Authentication auth) {
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
