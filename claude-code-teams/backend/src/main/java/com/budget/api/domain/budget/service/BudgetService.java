package com.budget.api.domain.budget.service;

import com.budget.api.domain.budget.dto.BudgetCreateRequest;
import com.budget.api.domain.budget.dto.BudgetResponse;
import com.budget.api.domain.budget.entity.Budget;
import com.budget.api.domain.budget.repository.BudgetRepository;
import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.repository.CategoryRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
                userId, request.getCategoryId(), request.getYear(), request.getMonth()
        ).ifPresent(b -> {
            throw new CustomException(ErrorCode.DUPLICATE_BUDGET);
        });

        Budget budget = Budget.create(user, category, request.getAmount(), request.getYear(), request.getMonth());
        Budget savedBudget = budgetRepository.save(budget);
        return BudgetResponse.from(savedBudget);
    }

    public List<BudgetResponse> getMonthlyBudgets(Long userId, Integer year, Integer month) {
        List<Budget> budgets = budgetRepository.findByUserIdAndYearAndMonth(userId, year, month);
        return budgets.stream()
                .map(BudgetResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, Long amount) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new CustomException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        budget.updateAmount(amount);
        return BudgetResponse.from(budget);
    }
}
