package com.budget.api.domain.expense.controller;

import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.service.ExpenseService;
import com.budget.api.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@Tag(name = "Expense", description = "지출 API")
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseCreateRequest request) {
        Long userId = getCurrentUserId();
        ExpenseResponse response = expenseService.createExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "지출이 등록되었습니다."));
    }

    @Operation(summary = "지출 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpenses(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<ExpenseResponse> response = expenseService.getExpenses(userId, year, month, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 상세 조회")
    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
            @PathVariable Long expenseId) {
        Long userId = getCurrentUserId();
        ExpenseResponse response = expenseService.getExpense(userId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 수정")
    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseUpdateRequest request) {
        Long userId = getCurrentUserId();
        ExpenseResponse response = expenseService.updateExpense(userId, expenseId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "지출이 수정되었습니다."));
    }

    @Operation(summary = "지출 삭제")
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long expenseId) {
        Long userId = getCurrentUserId();
        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.ok(ApiResponse.success(null, "지출이 삭제되었습니다."));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
