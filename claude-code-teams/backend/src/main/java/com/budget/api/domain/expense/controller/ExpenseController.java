package com.budget.api.domain.expense.controller;

import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.service.ExpenseService;
import com.budget.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Expense", description = "지출 API")
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @RequestParam Long userId,
            @Valid @RequestBody ExpenseCreateRequest request) {
        ExpenseResponse response = expenseService.createExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(summary = "지출 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(@RequestParam Long userId) {
        List<ExpenseResponse> response = expenseService.getExpenses(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "월별 지출 조회")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getMonthlyExpenses(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        List<ExpenseResponse> response = expenseService.getMonthlyExpenses(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 삭제")
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @RequestParam Long userId,
            @PathVariable Long expenseId) {
        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
