package com.budget.api.domain.expense.controller;

import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.service.ExpenseService;
import com.budget.api.global.common.ApiResponse;
import com.budget.api.global.security.CustomUserPrincipal;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // RBAC: 역할만 체크. ABAC(부서)는 서비스에서 principal.departmentId로 강제 주입.
    @Operation(summary = "지출 등록")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ExpenseCreateRequest request) {
        ExpenseResponse response = expenseService.createExpense(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "지출이 등록되었습니다."));
    }

    // 목록 조회는 단순 SpEL(RBAC)로만 걸고, 부서 필터링은 서비스에서.
    // — PermissionEvaluator 미사용 버전(학습 비교용)
    @Operation(summary = "지출 목록 조회")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpenses(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ExpenseResponse> response = expenseService.getExpenses(principal, year, month, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 상세/수정/삭제는 ABAC 포함한 커스텀 PermissionEvaluator에 위임.
    @Operation(summary = "지출 상세 조회")
    @PreAuthorize("hasPermission(#expenseId, 'Expense', 'READ')")
    @GetMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(
            @PathVariable Long expenseId) {
        ExpenseResponse response = expenseService.getExpense(expenseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "지출 수정")
    @PreAuthorize("hasPermission(#expenseId, 'Expense', 'WRITE')")
    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseUpdateRequest request) {
        ExpenseResponse response = expenseService.updateExpense(expenseId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "지출이 수정되었습니다."));
    }

    @Operation(summary = "지출 삭제")
    @PreAuthorize("hasPermission(#expenseId, 'Expense', 'DELETE')")
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok(ApiResponse.success(null, "지출이 삭제되었습니다."));
    }
}
