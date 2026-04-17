package com.example.abac.domain.expense;

import com.example.abac.domain.expense.dto.CreateExpenseRequest;
import com.example.abac.domain.expense.dto.ExpenseResponse;
import com.example.abac.domain.expense.dto.UpdateExpenseRequest;
import com.example.abac.security.CustomUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request, CustomUserPrincipal principal) {
        Expense expense = new Expense(
                principal.getUserId(),
                principal.getDepartmentId(),
                request.amount(),
                request.description()
        );
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long id, UpdateExpenseRequest request) {
        Expense expense = requireById(id);
        if (!expense.isDraft()) {
            throw new IllegalStateException("Only DRAFT expenses can be edited. current=" + expense.getStatus());
        }
        expense.updateDraft(request.amount(), request.description());
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void delete(Long id) {
        Expense expense = requireById(id);
        if (!expense.isDraft()) {
            throw new IllegalStateException("Only DRAFT expenses can be deleted. current=" + expense.getStatus());
        }
        expenseRepository.delete(expense);
    }

    @Transactional
    public ExpenseResponse submit(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.DRAFT, ExpenseStatus.SUBMITTED);
        expense.changeStatus(ExpenseStatus.SUBMITTED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse approve(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.SUBMITTED, ExpenseStatus.APPROVED);
        expense.changeStatus(ExpenseStatus.APPROVED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse reject(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.SUBMITTED, ExpenseStatus.REJECTED);
        expense.changeStatus(ExpenseStatus.REJECTED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse pay(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.APPROVED, ExpenseStatus.PAID);
        expense.changeStatus(ExpenseStatus.PAID);
        return ExpenseResponse.from(expense);
    }

    private static void assertStatus(Expense expense, ExpenseStatus expected, ExpenseStatus next) {
        if (expense.getStatus() != expected) {
            throw new IllegalStateException(
                    "Invalid transition: " + expense.getStatus() + " -> " + next
                            + " (expected current=" + expected + ")"
            );
        }
    }

    public ExpenseResponse findById(Long id, CustomUserPrincipal principal) {
        Specification<Expense> spec = ExpenseSpecifications.visibleTo(principal)
                .and(ExpenseSpecifications.hasId(id));
        return expenseRepository.findOne(spec)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    }

    public Page<ExpenseResponse> findAll(CustomUserPrincipal principal, Pageable pageable) {
        return expenseRepository.findAll(ExpenseSpecifications.visibleTo(principal), pageable)
                .map(ExpenseResponse::from);
    }

    Expense requireById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    }
}
