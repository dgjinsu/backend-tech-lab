package com.budget.api.domain.expense.service;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.repository.CategoryRepository;
import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.expense.repository.ExpenseRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
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

    @Transactional
    public ExpenseResponse createExpense(Long userId, ExpenseCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Expense expense = Expense.create(
                user,
                category,
                request.amount(),
                request.description(),
                request.expenseDate(),
                request.memo()
        );

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseResponse.from(savedExpense);
    }

    public Page<ExpenseResponse> getExpenses(Long userId, Integer year, Integer month, Long categoryId, Pageable pageable) {
        int resolvedYear = (year != null) ? year : LocalDate.now().getYear();
        int resolvedMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(resolvedYear, resolvedMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        if (categoryId != null) {
            return expenseRepository.findByUserIdAndExpenseDateBetweenAndCategoryId(
                    userId, startDate, endDate, categoryId, pageable
            ).map(ExpenseResponse::from);
        }

        return expenseRepository.findByUserIdAndExpenseDateBetween(
                userId, startDate, endDate, pageable
        ).map(ExpenseResponse::from);
    }

    public ExpenseResponse getExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseUpdateRequest request) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
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
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPENSE_NOT_FOUND));

        expenseRepository.delete(expense);
    }
}
