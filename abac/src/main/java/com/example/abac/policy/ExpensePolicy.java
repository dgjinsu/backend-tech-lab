package com.example.abac.policy;

import com.example.abac.domain.expense.Expense;
import com.example.abac.domain.expense.ExpenseRepository;
import com.example.abac.domain.expense.ExpenseStatus;
import com.example.abac.domain.user.Role;
import com.example.abac.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// [ABAC 단일 진실 소스] 이 프로젝트에서 '누가 어떤 액션을 할 수 있는가'의 규칙은 오직 이 Bean에서만 바뀐다.
// 컨트롤러 @PreAuthorize가 "@expensePolicy.canX(...)" 형태로 이 Bean을 SpEL로 호출.
// Bean 이름("expensePolicy")이 SpEL의 '@expensePolicy'와 정확히 매칭되어야 한다.
// readOnly 트랜잭션: 내부에서 expenseRepository.findById를 돌리므로 영속성 컨텍스트가 필요하지만, 쓰기는 안 함.
@Component("expensePolicy")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpensePolicy {

    // [ABAC] MANAGER 승인 한도. 초과건은 MANAGER 권한을 막고 ADMIN에게만 통과시킴.
    // 금액(리소스 속성) × 역할(주체 속성) 조합으로 판정한다는 점에서 RBAC을 넘은 ABAC 판단.
    public static final BigDecimal MANAGER_APPROVAL_LIMIT = new BigDecimal("1000000");

    private final ExpenseRepository expenseRepository;

    // DRAFT일 때만, 본인 소유일 때만 수정 가능. ADMIN은 속성 조건 없이 통과(관리자 오버라이드).
    public boolean canEditDraft(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        return expenseRepository.findById(id)
                .map(e -> e.getOwnerId().equals(p.getUserId()) && e.isDraft())
                .orElse(false);
    }

    // 제출도 수정과 동일 조건 — '내 DRAFT만 내가 제출'이라는 불변.
    public boolean canSubmit(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        return expenseRepository.findById(id)
                .map(e -> e.getOwnerId().equals(p.getUserId()) && e.isDraft())
                .orElse(false);
    }

    // [ABAC 판정의 하이라이트] 주체 역할 + 부서 + 금액 한도 + 상태, 네 속성의 AND.
    // 한 조건만 빠져도 false → 403. MANAGER는 자기 부서 SUBMITTED 건 중 한도 이내만 승인 가능.
    public boolean canApprove(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        if (p.getRole() != Role.MANAGER) return false;
        return expenseRepository.findById(id)
                .map(e -> sameDept(e, p)
                        && e.getStatus() == ExpenseStatus.SUBMITTED
                        && withinManagerLimit(e))
                .orElse(false);
    }

    // 반려도 승인과 같은 권한 집합을 쓴다. 규칙 한 곳에서 바꾸면 두 액션이 같이 조정되도록 재사용.
    public boolean canReject(Long id, Authentication authentication) {
        return canApprove(id, authentication);
    }

    // 지급은 역할 축이 바뀐다: FINANCE만(또는 ADMIN), 상태는 APPROVED 일 때.
    // 부서 조건 없음 — 경리팀은 전사 정산을 처리해야 하므로.
    public boolean canPay(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;
        if (p.getRole() != Role.FINANCE) return false;
        return expenseRepository.findById(id)
                .map(e -> e.getStatus() == ExpenseStatus.APPROVED)
                .orElse(false);
    }

    // 주체 속성(principal.departmentId) × 리소스 속성(expense.departmentId) 비교 — 전형적 ABAC.
    private static boolean sameDept(Expense e, CustomUserPrincipal p) {
        return e.getDepartmentId().equals(p.getDepartmentId());
    }

    // BigDecimal 비교는 compareTo <= 0. equals()는 scale 차이로 오판할 수 있어 쓰지 않는다.
    private static boolean withinManagerLimit(Expense e) {
        return e.getAmount().compareTo(MANAGER_APPROVAL_LIMIT) <= 0;
    }

    // SpEL에서 authentication 파라미터로 넘어온 Authentication → 우리 Principal로 캐스팅.
    // JwtAuthenticationFilter가 Principal을 CustomUserPrincipal로 심어두기 때문에 안전.
    private static CustomUserPrincipal principal(Authentication auth) {
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
