package com.example.abac.domain.expense;

import com.example.abac.security.CustomUserPrincipal;
import org.springframework.data.jpa.domain.Specification;

// [학습 포인트] '데이터 스코프' 축 = ABAC가 쿼리에 직접 개입하는 곳.
// @PreAuthorize가 '이 액션을 할 수 있느냐'를 막는 게이트라면, Specification은
// '조회 결과에 무엇이 포함되느냐'를 결정한다. 둘이 만나는 규칙:
//   - 액션: 메서드 진입 전(Policy) 차단
//   - 데이터: 쿼리 WHERE에 Predicate 주입해 애초에 DB에서 걸러냄
public final class ExpenseSpecifications {

    // 정적 팩토리 전용 유틸 → 인스턴스화 막음.
    private ExpenseSpecifications() {
    }

    // 역할별 '볼 수 있는 범위'를 Predicate로 돌려준다. ExpenseService가 이 값을
    // Specification.where(...).and(hasId(id)) 형태로 조합해 findOne/findAll에 건넨다.
    public static Specification<Expense> visibleTo(CustomUserPrincipal principal) {
        return switch (principal.getRole()) {
            // ADMIN: 제약 없음. conjunction()은 "항상 참"인 Predicate → WHERE 절에 영향 없음.
            case ADMIN -> (root, query, cb) -> cb.conjunction();
            // FINANCE: 전사를 보지만 결재 완료(APPROVED) 이후 건만. DRAFT/SUBMITTED 훔쳐보기 금지.
            case FINANCE -> (root, query, cb) -> root.get("status")
                    .in(ExpenseStatus.APPROVED, ExpenseStatus.PAID);
            // MANAGER: 본인 부서 전체. 타 부서 건은 쿼리 단계에서 제외 → 404로 귀결.
            case MANAGER -> (root, query, cb) -> cb.equal(
                    root.get("departmentId"), principal.getDepartmentId());
            // EMPLOYEE: 본인 작성분만.
            case EMPLOYEE -> (root, query, cb) -> cb.equal(
                    root.get("ownerId"), principal.getUserId());
        };
    }

    // 단건 조회용. visibleTo()와 and()로 합쳐서 "내가 볼 수 있는 것 중 id=X"를 만든다.
    // 이러면 타인의 건을 id로 찍어도 빈 결과 → Service가 EntityNotFoundException → 404.
    public static Specification<Expense> hasId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
