package com.budget.api.domain.salary.service;

import com.budget.api.domain.salary.dto.FixedExpenseRequest;
import com.budget.api.domain.salary.dto.SalaryCreateRequest;
import com.budget.api.domain.salary.dto.SalaryListResponse;
import com.budget.api.domain.salary.dto.SalaryResponse;
import com.budget.api.domain.salary.dto.SalaryUpdateRequest;
import com.budget.api.domain.salary.entity.FixedExpense;
import com.budget.api.domain.salary.entity.Salary;
import com.budget.api.domain.salary.repository.SalaryRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public SalaryResponse createSalary(Long userId, SalaryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        salaryRepository.findByUserIdAndYearAndMonth(userId, request.year(), request.month())
                .ifPresent(s -> {
                    throw new CustomException(ErrorCode.DUPLICATE_SALARY);
                });

        Salary salary = Salary.create(
                user,
                request.totalAmount(),
                request.year(),
                request.month(),
                request.memo()
        );

        if (request.fixedExpenses() != null) {
            request.fixedExpenses().forEach(fe -> {
                FixedExpense fixedExpense = FixedExpense.builder()
                        .name(fe.name())
                        .amount(fe.amount())
                        .build();
                salary.addFixedExpense(fixedExpense);
            });
        }

        Salary savedSalary = salaryRepository.save(salary);
        return SalaryResponse.from(savedSalary);
    }

    public List<SalaryListResponse> getSalaries(Long userId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        List<Salary> salaries = salaryRepository.findByUserIdAndYear(userId, year);
        return salaries.stream()
                .map(SalaryListResponse::from)
                .toList();
    }

    public SalaryResponse getSalary(Long userId, Long salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        if (!salary.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return SalaryResponse.from(salary);
    }

    @Transactional
    public SalaryResponse updateSalary(Long userId, Long salaryId, SalaryUpdateRequest request) {
        Salary salary = salaryRepository.findByIdAndUserId(salaryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        List<FixedExpense> newFixedExpenses = null;
        if (request.fixedExpenses() != null) {
            newFixedExpenses = request.fixedExpenses().stream()
                    .map(fe -> FixedExpense.builder()
                            .name(fe.name())
                            .amount(fe.amount())
                            .build())
                    .toList();
        }

        salary.update(request.totalAmount(), request.memo(), newFixedExpenses);
        return SalaryResponse.from(salary);
    }

    @Transactional
    public void deleteSalary(Long userId, Long salaryId) {
        Salary salary = salaryRepository.findByIdAndUserId(salaryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        salaryRepository.delete(salary);
    }
}
