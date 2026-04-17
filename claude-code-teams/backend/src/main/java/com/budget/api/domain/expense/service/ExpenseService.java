package com.budget.api.domain.expense.service;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.repository.CategoryRepository;
import com.budget.api.domain.department.entity.Department;
import com.budget.api.domain.department.repository.DepartmentRepository;
import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.expense.repository.ExpenseRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import com.budget.api.global.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public ExpenseResponse createExpense(CustomUserPrincipal principal, ExpenseCreateRequest request) {
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // ABAC: 부서는 principal의 값으로 강제 — 요청 바디로 덮어쓸 수 없게
        Department department = departmentRepository.findById(principal.departmentId())
                .orElseThrow(() -> new CustomException(ErrorCode.DEPARTMENT_NOT_FOUND));

        Expense expense = Expense.create(
                user,
                category,
                department,
                request.amount(),
                request.description(),
                request.expenseDate(),
                request.memo()
        );

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseResponse.from(savedExpense);
    }

    public Page<ExpenseResponse> getExpenses(CustomUserPrincipal principal, Integer year, Integer month,
                                             Long categoryId, Pageable pageable) {
        int resolvedYear = (year != null) ? year : LocalDate.now().getYear();
        int resolvedMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(resolvedYear, resolvedMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        if (principal.isAdmin()) {
            Page<Expense> page = (categoryId != null)
                    ? expenseRepository.findByExpenseDateBetweenAndCategoryId(startDate, endDate, categoryId, pageable)
                    : expenseRepository.findByExpenseDateBetween(startDate, endDate, pageable);
            return page.map(ExpenseResponse::from);
        }

        // EMPLOYEE / MANAGER: 부서 스코프
        Page<Expense> page = (categoryId != null)
                ? expenseRepository.findByDepartmentIdAndExpenseDateBetweenAndCategoryId(
                        principal.departmentId(), startDate, endDate, categoryId, pageable)
                : expenseRepository.findByDepartmentIdAndExpenseDateBetween(
                        principal.departmentId(), startDate, endDate, pageable);
        return page.map(ExpenseResponse::from);
    }

    public ExpenseResponse getExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        expense.update(
                category,
                request.amount(),
                request.description(),
                request.expenseDate(),
                request.memo()
        );

        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        expenseRepository.delete(expense);
    }
}
