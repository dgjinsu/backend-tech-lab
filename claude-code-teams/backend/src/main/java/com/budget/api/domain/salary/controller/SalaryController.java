package com.budget.api.domain.salary.controller;

import com.budget.api.domain.salary.dto.SalaryCreateRequest;
import com.budget.api.domain.salary.dto.SalaryListResponse;
import com.budget.api.domain.salary.dto.SalaryResponse;
import com.budget.api.domain.salary.dto.SalaryUpdateRequest;
import com.budget.api.domain.salary.service.SalaryService;
import com.budget.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
            @Valid @RequestBody SalaryCreateRequest request) {
        Long userId = getCurrentUserId();
        SalaryResponse response = salaryService.createSalary(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "급여가 등록되었습니다."));
    }

    @Operation(summary = "급여 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryListResponse>>> getSalaries(
            @RequestParam(required = false) Integer year) {
        Long userId = getCurrentUserId();
        List<SalaryListResponse> response = salaryService.getSalaries(userId, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "급여 상세 조회")
    @GetMapping("/{salaryId}")
    public ResponseEntity<ApiResponse<SalaryResponse>> getSalary(
            @PathVariable Long salaryId) {
        Long userId = getCurrentUserId();
        SalaryResponse response = salaryService.getSalary(userId, salaryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "급여 수정")
    @PutMapping("/{salaryId}")
    public ResponseEntity<ApiResponse<SalaryResponse>> updateSalary(
            @PathVariable Long salaryId,
            @Valid @RequestBody SalaryUpdateRequest request) {
        Long userId = getCurrentUserId();
        SalaryResponse response = salaryService.updateSalary(userId, salaryId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "급여가 수정되었습니다."));
    }

    @Operation(summary = "급여 삭제")
    @DeleteMapping("/{salaryId}")
    public ResponseEntity<ApiResponse<Void>> deleteSalary(
            @PathVariable Long salaryId) {
        Long userId = getCurrentUserId();
        salaryService.deleteSalary(userId, salaryId);
        return ResponseEntity.ok(ApiResponse.success(null, "급여가 삭제되었습니다."));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
