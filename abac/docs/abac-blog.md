# RBAC + ABAC 하이브리드 권한 체계 — 경비 승인 시스템으로 배우기

> Spring Boot + JPA Specification + SpEL로 **역할**과 **속성**을 함께 다루는 법

---

## 1. 왜 RBAC만으로는 부족한가

"MANAGER가 승인한다"는 **역할(Role) 기반 권한(RBAC)**만으로는 표현이 안 되는 규칙들이 있다.

- 같은 MANAGER라도 **자기 부서 건만** 승인 가능해야 한다
- MANAGER가 승인할 수 있는 **금액에 한도**가 있어야 한다 (예: 100만원 초과는 ADMIN만)
- EMPLOYEE는 **자기가 작성한** 경비만 목록에 보여야 한다

이런 규칙의 공통점은 "역할" 하나로는 답이 안 나오고, **요청자·리소스·환경의 속성(attribute)**을 함께 봐야 한다는 것이다.

→ 여기서 **ABAC (Attribute Based Access Control)**가 등장한다.

---

## 2. ABAC이란

**"누가(주체) 무엇(리소스)을 어떤 환경 조건에서 할 수 있는가"를 속성 조합으로 판정**하는 접근 제어 모델.

- **주체 속성 (Subject)**: `userId`, `role`, `departmentId` …
- **리소스 속성 (Resource)**: `ownerId`, `departmentId`, `amount`, `status` …
- **환경 속성 (Environment)**: 시간, IP … (본 프로젝트에선 미사용)
- **액션 (Action)**: 작성 / 수정 / 제출 / 승인 / 지급 / 조회

규칙 예시:

> **같은 부서의** MANAGER가, **SUBMITTED** 상태의, **100만원 이하** 경비를 승인할 수 있다

→ `role` × `departmentId` × `status` × `amount` 네 속성의 AND.

RBAC가 *"무엇을 할 수 있는 역할인가"*라면, ABAC는 *"이 특정 건에 대해 할 수 있는가"*를 결정한다. 둘은 대체 관계가 아니라 **겹쳐서 동작**한다.

---

## 3. 도메인: 경비 승인 워크플로우

교과서적인 예제. 경비 문서 한 장이 다음 상태를 밟는다.

```
DRAFT ──submit──▶ SUBMITTED ──approve──▶ APPROVED ──pay──▶ PAID
                         └──reject──▶ REJECTED
```

### 역할 5가지
`EMPLOYEE` · `MANAGER` · `FINANCE` · `ADMIN`

### 상태 5가지

| 상태 | 의미 | 머물 때 가능한 액션 |
|---|---|---|
| **DRAFT** | 초안·임시저장. 결재 요청 전 | 작성자 본인만 수정/삭제/제출 |
| **SUBMITTED** | 제출됨. 검토 대기 | 같은 부서 MANAGER(또는 ADMIN)가 승인/반려 |
| **APPROVED** | 승인됨 | FINANCE(또는 ADMIN)가 지급 |
| **REJECTED** | 반려됨 (종착) | — |
| **PAID** | 지급 완료 (종착) | — |

### 권한 매트릭스 — RBAC + ABAC 한 표

| Action | EMPLOYEE | MANAGER | FINANCE | ADMIN | ABAC 조건 |
|---|---|---|---|---|---|
| 작성 | O | O | O | O | - |
| 수정/삭제 | 본인+DRAFT | 본인+DRAFT | 본인+DRAFT | O | `ownerId=me && status=DRAFT` |
| 제출 | 본인+DRAFT | 본인+DRAFT | 본인+DRAFT | O | `ownerId=me && status=DRAFT` |
| 승인/반려 | X | O | X | O | `sameDept && amount ≤ 1,000,000 && status=SUBMITTED` |
| 지급 | X | X | O | O | `status=APPROVED` |
| 조회 | 본인 건 | 자기 부서 | APPROVED 이상 전사 | 전체 | Specification 자동 주입 |

---

## 4. 두 축의 강제 지점 — 이 그림이 모든 것이다

ABAC을 코드에 녹일 때 핵심은 **"어디에서 막을 것인가"**. 두 지점에서 동시에 강제해야 빈틈이 없다.

```
HTTP Request
  └─▶ JwtAuthenticationFilter (토큰 → Principal)
       └─▶ SecurityContext
            └─▶ Controller.method
                 [@PreAuthorize("@expensePolicy.canApprove(#id, authentication)")]  ◀── 액션 게이트
                 └─▶ ExpenseService (상태 전이 가드, 트랜잭션)
                      └─▶ ExpenseRepository.findAll(visibleTo(principal))           ◀── 데이터 스코프
                           └─▶ Hibernate WHERE 절 자동 주입 → DB
```

### 축 1: 액션 게이트 — `@PreAuthorize` + Policy Bean

> "**이 메서드를 호출해도 되는가**"를 **메서드 진입 전**에 판정. false면 `AuthorizationDeniedException` → **403**.

### 축 2: 데이터 스코프 — JPA `Specification`

> "**조회 결과에 무엇이 포함되는가**"를 **쿼리 WHERE 절**에 주입. 안 보이면 **404**.

### 왜 둘 다 필요한가

|  | 액션 게이트만 있을 때 | 데이터 스코프만 있을 때 |
|---|---|---|
| 문제 | 목록 조회에서 타인 건이 다 섞여 나옴 | 상태 변경 액션의 복잡한 조건(부서+금액+상태) 표현 곤란 |
| 보완 | Specification이 쿼리 단계에서 거름 | Policy가 진입 전 차단 |

### 비유

- **Policy** = 건물 입구의 **경비원** (출입증 검사 → 들여보낼지 말지)
- **Specification** = 엘리베이터의 **층 제한** (갈 수 있는 층만 눌림)

경비원만 있으면 위험한 층도 누를 수 있고, 층 제한만 있으면 애초에 위험인물이 건물에 들어와 있다.

---

## 5. 코드: `ExpensePolicy` — ABAC 규칙 단일 진실 소스

```java
@Component("expensePolicy")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpensePolicy {

    public static final BigDecimal MANAGER_APPROVAL_LIMIT = new BigDecimal("1000000");

    private final ExpenseRepository expenseRepository;

    public boolean canApprove(Long id, Authentication authentication) {
        CustomUserPrincipal p = principal(authentication);
        if (p.getRole() == Role.ADMIN) return true;             // 관리자 오버라이드
        if (p.getRole() != Role.MANAGER) return false;
        return expenseRepository.findById(id)
                .map(e -> sameDept(e, p)
                        && e.getStatus() == ExpenseStatus.SUBMITTED
                        && withinManagerLimit(e))
                .orElse(false);
    }

    // 주체 속성(principal.departmentId) × 리소스 속성(expense.departmentId) 비교 — 전형적 ABAC
    private static boolean sameDept(Expense e, CustomUserPrincipal p) {
        return e.getDepartmentId().equals(p.getDepartmentId());
    }

    private static boolean withinManagerLimit(Expense e) {
        return e.getAmount().compareTo(MANAGER_APPROVAL_LIMIT) <= 0;
    }

    private static CustomUserPrincipal principal(Authentication auth) {
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
```

### 설계 포인트

- `@Component("expensePolicy")`의 Bean 이름 `"expensePolicy"`가 **SpEL에서 `@expensePolicy`로 참조**된다. 이름이 안 맞으면 런타임 에러.
- `canApprove` 하나가 네 속성(`role`, `departmentId`, `status`, `amount`)의 **AND**. 한 조건만 빠져도 false.
- **ADMIN 오버라이드**는 맨 앞에서 조기 리턴 → 규칙 단순화.
- 규칙이 바뀌면 **오직 이 파일만** 수정. "단일 진실 소스(Single Source of Truth)".

---

## 6. 코드: Controller의 `@PreAuthorize` + SpEL

```java
@PostMapping("/{id}/approve")
@PreAuthorize("@expensePolicy.canApprove(#id, authentication)")
public ResponseEntity<ExpenseResponse> approve(@PathVariable Long id) {
    return ResponseEntity.ok(expenseService.approve(id));
}
```

### SpEL 해석

| 표현 | 뜻 |
|---|---|
| `@expensePolicy` | Spring ApplicationContext에서 **Bean 이름**으로 참조 (`@Component("expensePolicy")`와 매칭) |
| `#id` | 메서드 파라미터 이름 — `-parameters` 컴파일 옵션이나 Spring의 기본 파라미터 이름 해석으로 잡힘 |
| `authentication` | `SecurityContext`의 현재 `Authentication`을 **자동 주입** |

`@PreAuthorize`가 `false`를 받으면 `AuthorizationDeniedException` → `GlobalExceptionHandler`가 403 변환.

### RBAC 간단 규칙은 SpEL만으로

```java
@PreAuthorize("isAuthenticated()")     // 로그인만 확인 (RBAC 최소)
@PreAuthorize("hasRole('FINANCE')")    // 역할 체크
@PreAuthorize("@expensePolicy.canApprove(#id, authentication)")   // ABAC Bean 호출
```

---

## 7. 코드: `ExpenseSpecifications` — 데이터 스코프

```java
public static Specification<Expense> visibleTo(CustomUserPrincipal principal) {
    return switch (principal.getRole()) {
        case ADMIN -> (root, query, cb) -> cb.conjunction();                          // WHERE TRUE
        case FINANCE -> (root, query, cb) -> root.get("status")
                .in(ExpenseStatus.APPROVED, ExpenseStatus.PAID);                      // status IN (...)
        case MANAGER -> (root, query, cb) -> cb.equal(
                root.get("departmentId"), principal.getDepartmentId());                // department_id = ?
        case EMPLOYEE -> (root, query, cb) -> cb.equal(
                root.get("ownerId"), principal.getUserId());                           // owner_id = ?
    };
}

public static Specification<Expense> hasId(Long id) {
    return (root, query, cb) -> cb.equal(root.get("id"), id);
}
```

서비스에서 조합:

```java
Specification<Expense> spec = ExpenseSpecifications.visibleTo(principal)
        .and(ExpenseSpecifications.hasId(id));
expenseRepository.findOne(spec);   // "내가 볼 수 있는 것 중 id = ?"
```

### 왜 404인가 — 정보 은닉 전략

타인의 건을 id로 찍으면 WHERE에 걸려 빈 결과 → `EntityNotFoundException` → **404**.

만약 403("권한 없음")을 주면 *"그 id는 존재한다"*는 사실이 유출된다. 404는 **리소스 존재 자체를 감춘다**.

---

## 8. 곁다리 지식 1 — JPA Criteria API의 `root` / `cb` / `query`

`Specification`의 람다 파라미터 세 개를 이해하는 게 모든 것의 기초.

| | 역할 | 비유 |
|---|---|---|
| `root` | `Root<Expense>` — FROM 트리의 뿌리. 필드·조인 경로 | **명사** (주어·목적어) |
| `cb` | `CriteriaBuilder` — 연산자·함수 팩토리 | **동사** (연산자) |
| `query` | `CriteriaQuery<?>` — 쿼리 전체 구조 (DISTINCT/ORDER BY/서브쿼리) | **전체 문장의 형태** |

예: `cb.equal(root.get("id"), 5)` = **명사(id) + 동사(=) + 명사(5)**.

### 한 줄 암기

> **root는 "어디", cb는 "어떻게", query는 "전체 구조"**

### `cb`의 주요 메서드

| 호출 | SQL |
|---|---|
| `cb.equal(a, b)` | `a = b` |
| `cb.notEqual / gt / lt / ge / le` | `!=`, `>`, `<`, `>=`, `<=` |
| `cb.and(p1, p2)` / `cb.or(...)` | `AND` / `OR` |
| `cb.isNull(x)` / `cb.isNotNull(x)` | `IS NULL` |
| `cb.like(x, "foo%")` | `LIKE` |
| `cb.between(x, a, b)` | `BETWEEN` |
| **`cb.conjunction()`** | **`1=1` (항상 참)** |
| `cb.disjunction()` | `1=0` (항상 거짓) |

### `cb.conjunction()`은 왜 필요한가

ADMIN은 전체를 본다 → WHERE 절이 **없어야** 한다. 하지만 `Specification`은 `Predicate`를 반드시 반환해야 한다(null은 모호). `cb.conjunction()`은 *항상 true*인 Predicate라 조건 없이도 계약을 만족시키고, 다른 조건과 `AND`로 합쳐져도 무해 (`TRUE AND X = X`).

### `root.get("status").in(...)` vs `cb.equal(...)` — 왜 생김새가 다른가

| 연산 | Path에 메서드 있음? | CriteriaBuilder에 있음? |
|---|---|---|
| `=` (equal) | ❌ **cb 필수** | ✅ `cb.equal` |
| `IN` | ✅ `.in(...)` | ✅ `cb.in(...).value(...)` |
| `IS NULL` | ✅ `.isNull()` | ✅ `cb.isNull(...)` |
| `>`, `<`, `LIKE` | ❌ **cb 필수** | ✅ `cb.gt`, `cb.like` |

이유: `=`은 **두 피연산자를 비교**하는 이항 연산이라 외부 공장(cb)에서 만들어야 하고, `IN`·`IS NULL`은 **한 값에 대한 단항 체크**라 Path 자체에 편의 메서드가 있다. 결과 SQL은 동일, **가독성 때문에 둘을 섞어 쓰는 것**.

### `query`는 언제 쓰나

이 프로젝트엔 등장 안 하지만, 아래 경우 필요:

- `query.distinct(true)` — JOIN으로 중복 행 제거
- `query.getResultType()` — Spring Data의 count 쿼리일 때 fetch join 생략
- `query.subquery(...)` — `EXISTS` / `IN (SELECT ...)`
- `query.orderBy(...)` — Specification 내부에서 정렬 강제

---

## 9. 곁다리 지식 2 — JWT + `SecurityContext` 흐름

무상태(JWT)인데 어떻게 SpEL의 `authentication`이 Principal을 볼까?

1. **로그인** — `POST /auth/login` → `AuthenticationManager` → JWT 발급. 이때 토큰 claim에 `userId` · `role` · `departmentId`를 **봉인**.
2. **이후 요청** — `Authorization: Bearer <token>` 헤더.
3. **`JwtAuthenticationFilter`** — `UsernamePasswordAuthenticationFilter` **앞에 삽입**되어 있다.
    - 토큰 서명·만료 검증, 파싱 → `CustomUserPrincipal` 복원
    - `SecurityContextHolder.getContext().setAuthentication(auth)` ← **이 한 줄이 모든 것을 가능케 함**
4. **컨트롤러 도달 직전** `@PreAuthorize`가 SecurityContext에서 Authentication을 꺼내 Policy Bean에 전달.

```java
// JwtAuthenticationFilter.doFilterInternal 핵심
CustomUserPrincipal principal = tokenProvider.parse(token);
UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        principal, null, principal.getAuthorities()
);
SecurityContextHolder.getContext().setAuthentication(auth);   // ← 핵심
```

### 왜 JWT claim에 role / departmentId도 넣는가

JWT는 **서명으로 위·변조가 차단**된다. Principal 복원 시 DB 왕복 없이 토큰만으로 ABAC 속성을 회복할 수 있다 → 완전한 stateless.

---

## 10. 곁다리 지식 3 — 상태 머신과 권한의 맞물림

ABAC 판정이 통과된 뒤에도 **"이 상태에서 그 액션이 가능한가"**라는 도메인 불변을 지켜야 한다. 이건 Policy의 책임이 아니라 **Service의 책임**.

```java
private static void assertStatus(Expense expense, ExpenseStatus expected, ExpenseStatus next) {
    if (expense.getStatus() != expected) {
        throw new IllegalStateException(
                "Invalid transition: " + expense.getStatus() + " -> " + next);
    }
}
```

- Policy가 우회되거나 Service가 다른 경로로 호출되어도 상태 전이 규칙은 깨지면 안 된다 → **이중 가드**.
- 실패 시 `IllegalStateException` → `GlobalExceptionHandler`가 **409 Conflict**로 변환.

---

## 11. Policy Bean 방식 vs `PermissionEvaluator`

Spring Security는 `PermissionEvaluator` 인터페이스로 `hasPermission(#id, 'approve')` 같은 표현을 제공하기도 한다. 이 프로젝트는 **Policy Bean 방식**을 택했다.

| | Policy Bean (`@bean.method`) | PermissionEvaluator |
|---|---|---|
| 가독성 | 메서드 이름으로 의도가 드러남 (`canApprove`) | 문자열 파라미터에 의도 숨겨짐 |
| 테스트 | Bean을 직접 주입해 단위 테스트 가능 | 인터페이스 구현체 전체를 써야 함 |
| 확장 | 메서드 추가만 하면 됨 | 하나의 `hasPermission`에 분기 몰림 |
| Spring 표준 | 관례 | 프레임워크 제공 |

학습 용도에서는 **Policy Bean**이 훨씬 직관적이다.

---

## 12. 정리 — 학습 포인트 여섯 개

1. **RBAC × ABAC 겹침** — `@PreAuthorize`에 `isAuthenticated()` (RBAC)와 `@bean.method(...)` (ABAC)가 자유롭게 섞인다.
2. **액션 게이트 + 데이터 스코프** — Policy는 호출 막기(403), Specification은 안 보이게 하기(404). 둘 중 하나라도 빠지면 구멍.
3. **Policy Bean 패턴** — SpEL `@beanName.method(...)`로 복잡한 규칙을 메서드명에 드러낸다.
4. **Principal은 ABAC 속성 창고** — `userId/role/departmentId`가 `SecurityContext`에 살아 있어야 Policy도 Specification도 작동한다.
5. **JPA Criteria의 세 파라미터** — root는 "어디", cb는 "어떻게", query는 "전체 구조".
6. **정보 은닉** — 타인 건에 403 대신 **404**를 주는 것이 민감 정보 유출 관점에서 안전.

---

## 13. 실습 팁 (로컬에서 확인)

### 로그로 SQL 직접 보기

`application.properties`:

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

역할 바꿔 로그인 후 `/expenses` 호출 → 콘솔에서 WHERE 절이 어떻게 달라지는지 눈으로 확인.

### 시나리오 수동 검증

```bash
# 1) 로그인 → 토큰 획득
TOKEN=$(curl -s -X POST localhost:8090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"emp1","password":"pass"}' | jq -r '.token')

# 2) 작성 → 제출 → 승인 → 지급 (토큰은 역할별로 교체)
curl -s -X POST localhost:8090/expenses -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" -d '{"amount":"50000","description":"lunch"}'
curl -s -X POST localhost:8090/expenses/1/submit  -H "Authorization: Bearer $EMP1_TOKEN"
curl -s -X POST localhost:8090/expenses/1/approve -H "Authorization: Bearer $MGR1_TOKEN"
curl -s -X POST localhost:8090/expenses/1/pay     -H "Authorization: Bearer $FIN1_TOKEN"
```

### 실패 케이스로 확인할 것

- `mgr1` 토큰으로 **타 부서** 경비 approve → 403
- `mgr1` 토큰으로 **한도 초과(2,000,000원)** 경비 approve → 403, 같은 건을 `admin1` 토큰으로 → 200
- `emp1` 토큰으로 **타인 경비** GET → 404 (권한 부족이 아니라 "없음"으로 응답)
- `fin1` 토큰으로 **SUBMITTED** 상태 pay → 403 (APPROVED 아님)
- 이미 APPROVED인 건에 다시 approve → 409 Conflict (상태 전이 위반)

---

## 14. 참고 자료

- Spring Security — [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- Spring Data JPA — `JpaSpecificationExecutor`
- Jakarta Persistence — Criteria API
- NIST SP 800-162 — *Guide to Attribute Based Access Control*
- OWASP — *Broken Access Control*
