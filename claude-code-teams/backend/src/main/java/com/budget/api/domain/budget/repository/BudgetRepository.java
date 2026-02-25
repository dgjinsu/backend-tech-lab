package com.budget.api.domain.budget.repository;

import com.budget.api.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);

    Optional<Budget> findByUserIdAndCategoryIdAndYearAndMonth(Long userId, Long categoryId, Integer year, Integer month);
}
