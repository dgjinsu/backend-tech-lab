package com.budget.api.domain.expense.repository;

import com.budget.api.domain.expense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Page<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Expense> findByUserIdAndExpenseDateBetweenAndCategoryId(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable);

    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    // 부서 스코프 (EMPLOYEE / MANAGER 용)
    Page<Expense> findByDepartmentIdAndExpenseDateBetween(Long departmentId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Expense> findByDepartmentIdAndExpenseDateBetweenAndCategoryId(Long departmentId, LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable);

    // 전체 (ADMIN 용)
    Page<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Expense> findByExpenseDateBetweenAndCategoryId(LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable);
}
