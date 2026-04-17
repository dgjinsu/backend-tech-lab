# ABAC — RBAC + ABAC 하이브리드 경비 관리 학습 프로젝트

## 목적

RBAC(역할 기반)과 ABAC(속성·데이터 스코프 기반) 권한 체계를 **결합**해보는 학습용 Spring Boot 프로젝트. 경비/지출 승인 워크플로우라는 교과서적 도메인에서 두 모델이 어떻게 겹쳐 동작하는지 손으로 쌓아가며 감을 잡는 것이 목표.

## 도메인 개요

- **User**: id, username, password, role, departmentId
- **Department**: id, name — (Engineering / Sales / Finance)
- **Expense**: id, ownerId, departmentId, amount, description, status, 타임스탬프
- **ExpenseStatus**: `DRAFT → SUBMITTED → APPROVED → PAID` (또는 `REJECTED`)
- **Role**: `EMPLOYEE`, `MANAGER`, `FINANCE`, `ADMIN`

## 권한 매트릭스

| Action | EMPLOYEE | MANAGER | FINANCE | ADMIN | ABAC 조건 |
|---|---|---|---|---|---|
| 작성 (POST) | O | O | O | O | - |
| 수정/삭제 | 본인+DRAFT | 본인+DRAFT | 본인+DRAFT | O | `ownerId=me && status=DRAFT` |
| 제출 (submit) | 본인+DRAFT | 본인+DRAFT | 본인+DRAFT | O | `ownerId=me && status=DRAFT` |
| 승인/반려 | X | O | X | O | `sameDept && amount ≤ 1,000,000 && status=SUBMITTED` |
| 지급 (pay) | X | X | O | O | `status=APPROVED` |
| 조회 (GET) | 본인 건 | 자기 부서 | APPROVED 이상 전사 | 전체 | Specification 자동 주입 |

MANAGER 승인 한도: **1,000,000원** (`ExpensePolicy.MANAGER_APPROVAL_LIMIT`). 한도 초과건은 ADMIN만 승인 가능.

## 두 축의 강제 지점

```
HTTP Request
  └─> JwtAuthenticationFilter (토큰 → CustomUserPrincipal)
       └─> SecurityContext
            └─> Controller.method
                 [@PreAuthorize("@expensePolicy.canApprove(#id, authentication)")]   <- 액션 게이트 (RBAC+ABAC)
                 └─> ExpenseService (상태 전이 가드, 트랜잭션)
                      └─> ExpenseRepository.findAll(visibleTo(principal), pageable)  <- 데이터 스코프 (ABAC)
                           └─> Hibernate WHERE 절 자동 주입 → DB
```

- **액션 권한**: `@PreAuthorize` + Policy Bean (`policy/ExpensePolicy.java`) — 메서드 진입 전 판정
- **데이터 스코프**: JPA `Specification` (`domain/expense/ExpenseSpecifications.java`) — 조회 쿼리 WHERE 주입

## 프로젝트 구조

```
com.example.abac
├── AbacApplication                   (@EnableMethodSecurity, @EnableJpaAuditing)
├── common/                            BaseTimeEntity, ApiError, GlobalExceptionHandler
├── config/                            SecurityConfig, DataInitializer(부서 3 + 사용자 5 시딩)
├── security/                          CustomUserPrincipal, CustomUserDetailsService,
│                                      AuthController, AuthService, jwt/{Provider, Filter}
├── domain/user                        User, Role, UserRepository
├── domain/department                  Department, DepartmentRepository
├── domain/expense                     Expense, ExpenseStatus, ExpenseRepository,
│                                      ExpenseService, ExpenseController, ExpenseSpecifications,
│                                      dto/{Create, Update, Response}
└── policy/                            ExpensePolicy (canEditDraft / canSubmit / canApprove / canReject / canPay)
```

## 실행 방법

```bash
./gradlew bootRun
```

- 포트: **8090** (`application.properties`의 `server.port`)
- H2 콘솔: `http://localhost:8090/h2-console` — JDBC URL `jdbc:h2:mem:abacdb`, user `sa`, no password
- 시딩된 계정 5개 (비밀번호 모두 `pass`):

| username | role | departmentId | 부서명 |
|---|---|---|---|
| emp1 | EMPLOYEE | 1 | Engineering |
| emp2 | EMPLOYEE | 2 | Sales |
| mgr1 | MANAGER | 1 | Engineering |
| fin1 | FINANCE | 3 | Finance |
| admin1 | ADMIN | 3 | Finance |

## 수동 테스트 (curl)

```bash
# 1) 로그인 → 토큰
TOKEN=$(curl -s -X POST localhost:8090/auth/login -H "Content-Type: application/json" \
  -d '{"username":"emp1","password":"pass"}' | python -c "import sys,json;print(json.load(sys.stdin)['token'])")

# 2) 지출 작성 (DRAFT)
curl -s -X POST localhost:8090/expenses -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"amount":"50000","description":"lunch"}'

# 3) 워크플로우: submit → approve → pay
# (토큰은 emp1 / mgr1 / fin1 로 각각 교체)
curl -s -X POST localhost:8090/expenses/1/submit  -H "Authorization: Bearer $EMP1_TOKEN"
curl -s -X POST localhost:8090/expenses/1/approve -H "Authorization: Bearer $MGR1_TOKEN"
curl -s -X POST localhost:8090/expenses/1/pay     -H "Authorization: Bearer $FIN1_TOKEN"
```

## 자동 테스트

```bash
./gradlew test
```

통합 테스트는 `src/test/java/com/example/abac/domain/expense/ExpenseIntegrationTest.java`. 역할별 12개 시나리오:

1. 인증 없음 → 401
2. EMPLOYEE 작성 → 201, DRAFT
3. EMPLOYEE 본인 건만 조회 (데이터 스코프)
4. MANAGER 자기 부서만 조회
5. ADMIN 전체 조회
6. EMPLOYEE 타인 단건조회 → 404 (Specification)
7. EMPLOYEE 타인 PATCH → 403 (Policy)
8. MANAGER 타 부서 approve → 403
9. MANAGER 한도 초과 approve → 403, ADMIN은 200
10. 해피 패스: submit → approve → pay
11. FINANCE가 SUBMITTED 지급 시도 → 403
12. 중복 approve → 403

## 학습 초점 — 어디를 관찰할까

1. **`@PreAuthorize` SpEL이 Bean을 해석하는 방식**: `@expensePolicy.canApprove(#id, authentication)` 에서 `@expensePolicy`는 `ExpensePolicy` Bean 이름. `authentication`은 `SecurityContextHolder`에서 자동 바인딩.
2. **`Specification` 조합**: `visibleTo(principal).and(hasId(id))` — 두 predicate을 AND로 합쳐 WHERE 절 만들기. 로그에서 SQL을 직접 확인 (`spring.jpa.show-sql=true`).
3. **Principal 추출 타이밍**: 필터(`JwtAuthenticationFilter`)에서 `SecurityContext`에 넣고, 컨트롤러에서 `@AuthenticationPrincipal CustomUserPrincipal`로 꺼내기.
4. **정책 Bean 방식 vs `PermissionEvaluator`**: 여기서는 Bean 방식 선택. 이유 — 메서드 이름으로 의도가 드러나고, 테스트 시 직접 호출 가능.

## 주요 파일 경로

- [SecurityConfig.java](src/main/java/com/example/abac/config/SecurityConfig.java) — Filter Chain, `anyRequest().authenticated()`, JWT 필터 삽입
- [ExpensePolicy.java](src/main/java/com/example/abac/policy/ExpensePolicy.java) — ABAC 규칙 단일 진실 소스
- [ExpenseSpecifications.java](src/main/java/com/example/abac/domain/expense/ExpenseSpecifications.java) — 데이터 스코프 predicate
- [ExpenseController.java](src/main/java/com/example/abac/domain/expense/ExpenseController.java) — `@PreAuthorize` 매핑이 한눈에 보이는 곳
- [ExpenseService.java](src/main/java/com/example/abac/domain/expense/ExpenseService.java) — 상태 전이 가드
- [ExpenseIntegrationTest.java](src/test/java/com/example/abac/domain/expense/ExpenseIntegrationTest.java) — 실행 가능한 권한 명세
- [DataInitializer.java](src/main/java/com/example/abac/config/DataInitializer.java) — 부서/사용자 시딩

## 운영 메모

- `./gradlew bootRun` 중단 후 포트 8090이 점유된 채 남는 경우가 있음 (gradle daemon → 자식 JVM orphan).
  - `netstat -ano | grep :8090` 로 PID 확인 후 `taskkill //PID <PID> //F`
