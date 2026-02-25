package com.budget.api.domain.budget.controller;

import com.budget.api.domain.budget.dto.BudgetCreateRequest;
import com.budget.api.domain.budget.dto.BudgetResponse;
import com.budget.api.domain.budget.service.BudgetService;
import com.budget.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Budget", description = "예산 API")
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(summary = "예산 설정")
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @RequestParam Long userId,
            @Valid @RequestBody BudgetCreateRequest request) {
        BudgetResponse response = budgetService.createBudget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(summary = "월별 예산 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getMonthlyBudgets(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        List<BudgetResponse> response = budgetService.getMonthlyBudgets(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "예산 수정")
    @PatchMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @RequestParam Long userId,
            @PathVariable Long budgetId,
            @RequestParam Long amount) {
        BudgetResponse response = budgetService.updateBudget(userId, budgetId, amount);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
