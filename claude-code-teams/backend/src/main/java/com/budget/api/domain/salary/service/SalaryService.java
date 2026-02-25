package com.budget.api.domain.salary.service;

import com.budget.api.domain.salary.dto.SalaryCreateRequest;
import com.budget.api.domain.salary.dto.SalaryResponse;
import com.budget.api.domain.salary.entity.Salary;
import com.budget.api.domain.salary.repository.SalaryRepository;
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
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public SalaryResponse createSalary(Long userId, SalaryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        salaryRepository.findByUserIdAndYearAndMonth(userId, request.getYear(), request.getMonth())
                .ifPresent(s -> {
                    throw new CustomException(ErrorCode.DUPLICATE_SALARY);
                });

        Salary salary = Salary.create(
                user,
                request.getAmount(),
                request.getYear(),
                request.getMonth(),
                request.getFixedExpense(),
                request.getMemo()
        );

        Salary savedSalary = salaryRepository.save(salary);
        return SalaryResponse.from(savedSalary);
    }

    public List<SalaryResponse> getSalaries(Long userId) {
        List<Salary> salaries = salaryRepository.findByUserId(userId);
        return salaries.stream()
                .map(SalaryResponse::from)
                .collect(Collectors.toList());
    }

    public SalaryResponse getSalary(Long userId, Integer year, Integer month) {
        Salary salary = salaryRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));
        return SalaryResponse.from(salary);
    }
}
