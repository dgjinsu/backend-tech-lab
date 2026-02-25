package com.budget.api.domain.salary.repository;

import com.budget.api.domain.salary.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    List<Salary> findByUserId(Long userId);

    Optional<Salary> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
}
