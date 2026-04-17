package com.example.abac.domain.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// [데이터 스코프 진입점] JpaSpecificationExecutor를 함께 상속해야
// findAll(Specification) / findOne(Specification)이 열린다.
// ExpenseSpecifications.visibleTo(principal)를 넘겨 WHERE 절을 런타임에 조립.
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
}
