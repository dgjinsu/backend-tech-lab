package com.example.abac.domain.expense;

import com.example.abac.domain.expense.dto.CreateExpenseRequest;
import com.example.abac.domain.expense.dto.ExpenseResponse;
import com.example.abac.domain.expense.dto.UpdateExpenseRequest;
import com.example.abac.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ExpenseResponse create(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return expenseService.create(request, principal);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@expensePolicy.canEditDraft(#id, authentication)")
    public ExpenseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        return expenseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@expensePolicy.canEditDraft(#id, authentication)")
    public void delete(@PathVariable Long id) {
        expenseService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ExpenseResponse get(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return expenseService.findById(id, principal);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<ExpenseResponse> list(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            Pageable pageable
    ) {
        return expenseService.findAll(principal, pageable);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("@expensePolicy.canSubmit(#id, authentication)")
    public ExpenseResponse submit(@PathVariable Long id) {
        return expenseService.submit(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@expensePolicy.canApprove(#id, authentication)")
    public ExpenseResponse approve(@PathVariable Long id) {
        return expenseService.approve(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("@expensePolicy.canReject(#id, authentication)")
    public ExpenseResponse reject(@PathVariable Long id) {
        return expenseService.reject(id);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("@expensePolicy.canPay(#id, authentication)")
    public ExpenseResponse pay(@PathVariable Long id) {
        return expenseService.pay(id);
    }
}
