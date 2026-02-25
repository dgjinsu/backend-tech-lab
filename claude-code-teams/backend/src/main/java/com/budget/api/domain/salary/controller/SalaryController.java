package com.budget.api.domain.salary.controller;

import com.budget.api.domain.salary.dto.SalaryCreateRequest;
import com.budget.api.domain.salary.dto.SalaryResponse;
import com.budget.api.domain.salary.service.SalaryService;
import com.budget.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Salary", description = "급여 API")
@RestController
@RequestMapping("/api/v1/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @Operation(summary = "급여 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<SalaryResponse>> createSalary(
            @RequestParam Long userId,
            @Valid @RequestBody SalaryCreateRequest request) {
        SalaryResponse response = salaryService.createSalary(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(summary = "급여 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryResponse>>> getSalaries(@RequestParam Long userId) {
        List<SalaryResponse> response = salaryService.getSalaries(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "월별 급여 조회")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<SalaryResponse>> getSalary(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        SalaryResponse response = salaryService.getSalary(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
