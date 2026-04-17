package com.example.abac.domain.expense;

import com.example.abac.domain.expense.dto.CreateExpenseRequest;
import com.example.abac.domain.expense.dto.ExpenseResponse;
import com.example.abac.domain.expense.dto.UpdateExpenseRequest;
import com.example.abac.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// [학습 지도] 액션 게이트가 한눈에 보이는 곳. 각 엔드포인트의 @PreAuthorize가
// RBAC(isAuthenticated)과 ABAC(@expensePolicy.canX)가 어떻게 선택되는지 한 줄씩 보여준다.
//
// SpEL 문법 요약:
//   - @expensePolicy : ApplicationContext에서 "expensePolicy"라는 이름의 Bean 참조
//   - #id            : 메서드 파라미터 이름 그대로 참조 (파라미터 이름 정보가 필요 → -parameters 옵션)
//   - authentication : SecurityContext에서 자동 주입되는 현재 Authentication
//
// @PreAuthorize false 리턴 → AuthorizationDeniedException → GlobalExceptionHandler가 403.
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // [작성] 누구나 작성 가능. ownerId/departmentId는 Principal에서만 주입되므로 위조 불가.
    // 201 Created + Location 헤더(/expenses/{새 id})로 REST 관례에 맞춤.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpenseResponse> create(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        ExpenseResponse body = expenseService.create(request, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(body.id())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    // [수정] 본인 + DRAFT만. ABAC 규칙은 ExpensePolicy.canEditDraft에 집중.
    @PatchMapping("/{id}")
    @PreAuthorize("@expensePolicy.canEditDraft(#id, authentication)")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    // [삭제] 수정과 동일한 권한. 한 Policy 메서드를 두 액션이 공유 → 규칙 변경 지점 단일화.
    // 204 No Content: 본문 없는 성공 응답. ResponseEntity<Void>로 타입 고정.
    @DeleteMapping("/{id}")
    @PreAuthorize("@expensePolicy.canEditDraft(#id, authentication)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // [단건 조회] 액션 게이트는 '로그인만' 통과. 실제 '볼 수 있는가'는 Service의 Specification이 걸러낸다
    // → 타인의 id로 찍으면 403이 아니라 404가 반환됨(리소스 존재 은닉).
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpenseResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(expenseService.findById(id, principal));
    }

    // [목록] 동일하게 Specification으로 역할별 범위 자동 제한.
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ExpenseResponse>> list(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            Pageable pageable
    ) {
        return ResponseEntity.ok(expenseService.findAll(principal, pageable));
    }

    // [제출] DRAFT → SUBMITTED. Policy.canSubmit이 본인 소유 + DRAFT 판정.
    @PostMapping("/{id}/submit")
    @PreAuthorize("@expensePolicy.canSubmit(#id, authentication)")
    public ResponseEntity<ExpenseResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.submit(id));
    }

    // [승인] ABAC 속성 4종(역할·부서·상태·금액) AND 조합 검사 지점. 학습 핵심 예제.
    @PostMapping("/{id}/approve")
    @PreAuthorize("@expensePolicy.canApprove(#id, authentication)")
    public ResponseEntity<ExpenseResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.approve(id));
    }

    // [반려] canApprove와 권한 집합 공유 — 규칙 한 군데서 바꾸면 양쪽 다 조정.
    @PostMapping("/{id}/reject")
    @PreAuthorize("@expensePolicy.canReject(#id, authentication)")
    public ResponseEntity<ExpenseResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.reject(id));
    }

    // [지급] FINANCE(또는 ADMIN) + APPROVED. 부서 제약 없음.
    @PostMapping("/{id}/pay")
    @PreAuthorize("@expensePolicy.canPay(#id, authentication)")
    public ResponseEntity<ExpenseResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.pay(id));
    }
}
