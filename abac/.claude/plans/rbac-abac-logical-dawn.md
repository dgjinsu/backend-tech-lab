# RBAC + ABAC 하이브리드 경비 관리 서비스 구축

## Context

`D:\backend-tech-lab\abac`는 Spring Boot 3.5.13 / Java 21 / Spring Security / JPA / H2 의존성만 포함된 빈 프로젝트이다. 사용자는 RBAC(역할 기반 접근 제어)와 ABAC(속성 기반 접근 제어, 데이터 스코프 포함)의 **결합 패턴**을 손으로 쌓아보며 학습하고자 한다. 이를 위해 경비/지출 관리라는 교과서적 도메인을 골랐다 — 역할 계층(사원/팀장/재무/관리자), 자연스러운 데이터 스코프(본인 → 팀 → 부서 → 전사), 속성 조건(금액 한도, 상태, 소유자, 부서 일치)이 모두 선명하게 드러나기 때문.

이전 commit(be02087)에 구현 히스토리가 있으나 **참고하지 않고 처음부터 새로 쌓는다** — 패턴을 스스로 구성하는 경험이 학습의 핵심.

## 학습 초점

1. `@PreAuthorize` SpEL이 Policy Bean(`@expensePolicy`)을 어떻게 해석하는지
2. `Specification` 조합으로 WHERE 절을 역할에 따라 동적으로 주입
3. `SecurityContextHolder`에서 Principal을 꺼내는 계층별 타이밍
4. RBAC(액션 게이트)과 ABAC(속성·데이터 스코프)이 같은 요청에서 어떻게 겹쳐 동작하는지

## 도메인 모델

- **User**: id, username, password(encoded), role, departmentId
- **Department**: id, name
- **Expense**: id, ownerId, departmentId, amount, description, status, createdAt, updatedAt
- **Role (enum)**: `EMPLOYEE`, `MANAGER`, `FINANCE`, `ADMIN`
- **ExpenseStatus (enum)**: `DRAFT` → `SUBMITTED` → `APPROVED` → `PAID`, 혹은 `REJECTED`

## 권한 매트릭스

| Action | EMPLOYEE | MANAGER | FINANCE | ADMIN | ABAC 조건 |
|---|---|---|---|---|---|
| 작성(POST) | O | O | O | O | - |
| 수정/삭제 | 본인+DRAFT | 본인+DRAFT | 본인+DRAFT | O | `ownerId=me && status=DRAFT` |
| 제출(submit) | 본인 | 본인 | 본인 | O | `ownerId=me && status=DRAFT` |
| 승인/반려 | X | O | X | O | `sameDept && amount ≤ 1,000,000 && status=SUBMITTED` |
| 지급(pay) | X | X | O | O | `status=APPROVED` |
| 조회(GET) | 본인 건 | 자기 부서 | APPROVED+ 전사 | 전체 | Specification 자동 주입 |

MANAGER 승인 한도: **1,000,000원** (상수로 정의, 한도 초과 건은 ADMIN만 승인).

## 권한 체크 흐름

```
HTTP Request
  └─> JwtAuthenticationFilter (토큰 → CustomUserPrincipal)
       └─> SecurityContext 주입
            └─> Controller.method
                 [@PreAuthorize("hasRole('MANAGER') and @expensePolicy.canApprove(#id, authentication)")]
                 └─> ExpenseService (상태전이 가드, 트랜잭션)
                      └─> ExpenseRepository.findAll(ExpenseSpecifications.visibleTo(principal), pageable)
                           └─> Hibernate WHERE 절 주입 → DB
```

두 축이 함께 작동:
- **액션 권한** (RBAC + ABAC): `@PreAuthorize`로 메서드 진입 차단
- **데이터 스코프** (ABAC): Specification으로 조회 결과 자동 필터링

## 패키지 구조

```
com.example.abac
├── AbacApplication.java              (@EnableMethodSecurity, @EnableJpaAuditing)
├── common/
│   ├── BaseTimeEntity.java
│   ├── ApiError.java
│   └── GlobalExceptionHandler.java   (403/404/409 통일)
├── config/
│   ├── SecurityConfig.java           (SecurityFilterChain, PasswordEncoder, AuthenticationManager)
│   └── JpaAuditingConfig.java
├── security/
│   ├── CustomUserPrincipal.java      (UserDetails 구현 + userId/departmentId/role 노출)
│   ├── CustomUserDetailsService.java
│   ├── AuthController.java           (/auth/login)
│   ├── AuthService.java
│   ├── dto/LoginRequest.java, LoginResponse.java
│   └── jwt/
│       ├── JwtTokenProvider.java     (jjwt 0.12.x, HS256, claims: userId/role/departmentId)
│       └── JwtAuthenticationFilter.java
├── domain/
│   ├── user/
│   │   ├── User.java, Role.java, UserRepository.java
│   ├── department/
│   │   ├── Department.java, DepartmentRepository.java
│   └── expense/
│       ├── Expense.java, ExpenseStatus.java
│       ├── ExpenseRepository.java            (extends JpaSpecificationExecutor)
│       ├── ExpenseSpecifications.java        (visibleTo(principal))
│       ├── ExpenseService.java
│       ├── ExpenseController.java
│       └── dto/ (CreateExpenseRequest, UpdateExpenseRequest, ExpenseResponse)
└── policy/
    └── ExpensePolicy.java            (@Component("expensePolicy"))
```

## 주요 클래스 책임

- **CustomUserPrincipal** — ABAC 속성(userId, departmentId, role) 공급원. 모든 정책이 이 객체로부터 속성을 읽는다.
- **JwtAuthenticationFilter** — `OncePerRequestFilter` 상속. Authorization 헤더 → 토큰 파싱 → Principal 복원 → SecurityContext 주입.
- **SecurityConfig** — `/auth/**` permitAll, 나머지 authenticated, STATELESS 세션, JWT 필터 체인 등록.
- **ExpensePolicy** — `canApprove / canReject / canPay / canEditDraft` 메서드. `@PreAuthorize` SpEL에서 호출하는 단일 진실 소스.
- **ExpenseSpecifications** — `visibleTo(principal)` 정적 메서드가 Role별 `Predicate` 생성. EMPLOYEE → `ownerId=me`, MANAGER → `departmentId=mine`, FINANCE → `status IN (APPROVED, PAID)`, ADMIN → 무필터.
- **ExpenseService** — 트랜잭션 경계, 상태 전이 가드(`IllegalStateException` → 409), Specification 적용.
- **ExpenseController** — REST 엔드포인트. 각 메서드마다 `@PreAuthorize` 명시.

## 마일스톤

각 단계는 독립적으로 컴파일·실행·수동 확인 가능한 단위로 구성.

### M1 — 뼈대 + User/Auth

**생성/수정 파일:**
- `build.gradle` — jjwt-api / jjwt-impl / jjwt-jackson `0.12.x` 3개 추가
- `src/main/resources/application.properties` — H2 콘솔, `jwt.secret`, `jwt.expire-ms`
- `src/main/resources/data.sql` — 부서 2개, 역할별 사용자 5명(EMPLOYEE 2 + MANAGER 1 + FINANCE 1 + ADMIN 1)
- `AbacApplication.java` — `@EnableMethodSecurity`, `@EnableJpaAuditing`
- `common/BaseTimeEntity.java`, `config/JpaAuditingConfig.java`
- `domain/department/Department.java`, `DepartmentRepository.java`
- `domain/user/User.java`, `Role.java`, `UserRepository.java`
- `security/CustomUserPrincipal.java`, `CustomUserDetailsService.java`
- `security/jwt/JwtTokenProvider.java`, `JwtAuthenticationFilter.java`
- `config/SecurityConfig.java`
- `security/AuthController.java`, `AuthService.java`, `dto/LoginRequest.java`, `LoginResponse.java`

**재사용할 Spring 유틸:** `UserDetailsService`, `UsernamePasswordAuthenticationToken`, `OncePerRequestFilter`, `BCryptPasswordEncoder`, `AuthenticationManager`.

**검증:** `./gradlew bootRun` → `POST /auth/login` 으로 5개 사용자 각각 JWT 발급 확인.

### M2 — Expense CRUD + RBAC

**생성 파일:**
- `domain/expense/Expense.java`, `ExpenseStatus.java`
- `domain/expense/ExpenseRepository.java` (`JpaRepository<Expense,Long>, JpaSpecificationExecutor<Expense>`)
- `domain/expense/ExpenseService.java`, `ExpenseController.java`
- `domain/expense/dto/CreateExpenseRequest.java`, `UpdateExpenseRequest.java`, `ExpenseResponse.java`
- `common/ApiError.java`, `GlobalExceptionHandler.java`

**이 단계 범위:** Role 기반 `@PreAuthorize`만 적용(`hasAnyRole(...)`). ABAC는 M3에서.

**엔드포인트:** `POST /expenses`, `PATCH /expenses/{id}`, `DELETE /expenses/{id}`, `GET /expenses`, `GET /expenses/{id}`.

**재사용:** `@EnableMethodSecurity(prePostEnabled = true)`, `@EntityListeners(AuditingEntityListener.class)`.

**검증:** EMPLOYEE 토큰으로 작성 가능, 타인 건 목록은 모두 보이는 상태(= M3 전 기대 동작).

### M3 — ABAC 정책 + Specification

**생성 파일:**
- `policy/ExpensePolicy.java` — `canEditDraft(id, auth)`, `canApprove(id, auth)`, `canReject(id, auth)`, `canPay(id, auth)`
- `domain/expense/ExpenseSpecifications.java` — `visibleTo(CustomUserPrincipal)`

**수정 파일:**
- `ExpenseController` — SpEL에 `@expensePolicy.canApprove(#id, authentication)` 등 주입
- `ExpenseService.findAll(principal, pageable)` — `repo.findAll(ExpenseSpecifications.visibleTo(principal), pageable)` 로 전환

**재사용:** `org.springframework.data.jpa.domain.Specification`, `CriteriaBuilder`, `@AuthenticationPrincipal`.

**검증:** EMPLOYEE가 `GET /expenses` 호출 시 본인 건만 반환. MANAGER는 자기 부서만.

### M4 — 결재 워크플로우

**수정 파일:**
- `ExpenseService` — `submit / approve / reject / pay` 메서드 추가. 상태 전이 가드(`DRAFT→SUBMITTED→APPROVED→PAID`, 잘못된 전이 시 `IllegalStateException` → 409).
- `ExpenseController` — `POST /expenses/{id}/submit`, `/approve`, `/reject`, `/pay` 엔드포인트. 각각에 `@PreAuthorize` + Policy 호출.

**검증:** 한도 초과 건을 MANAGER가 승인 시도 → 403, FINANCE가 SUBMITTED 상태 건 지급 시도 → 409.

### M5 — 통합 테스트 + 문서

**생성 파일:**
- `src/test/resources/application-test.properties` — 테스트용 H2, 별도 jwt.secret
- `src/test/java/com/example/abac/domain/expense/ExpenseIntegrationTest.java` — 8~10개 시나리오
- `CLAUDE.md` — 도메인 설명, 권한 매트릭스, 실행/테스트 명령, 학습 초점 포인트
- `README.md` — 간단 소개 + API 요약

**테스트 시나리오 (MockMvc):**
1. EMPLOYEE가 타인 작성 건 `GET /expenses/{id}` → 403 또는 404(정책에 따라 결정)
2. EMPLOYEE가 `GET /expenses` → 본인 건만
3. MANAGER가 타 부서 건 `POST /approve` → 403
4. MANAGER가 1,000,000원 초과 건 승인 → 403
5. FINANCE가 SUBMITTED 건 `POST /pay` → 409
6. FINANCE가 APPROVED 건 `POST /pay` → 200, 상태 PAID 전이
7. EMPLOYEE가 SUBMITTED 상태 본인 건 수정 시도 → 409
8. ADMIN은 모든 제한 무시 → 200
9. 인증 없음 → 401
10. MANAGER `GET /expenses` → 자기 부서 건만

**재사용:** `@SpringBootTest`, `MockMvc`, `@Transactional`, 테스트용 JWT 발급 helper(`login → token` 헬퍼 메서드).

## 검증 방법 (end-to-end)

1. **실행:** `./gradlew bootRun`
2. **H2 콘솔:** `http://localhost:8080/h2-console` (JDBC URL은 `application.properties` 확인)
3. **로그인:** `curl -X POST localhost:8080/auth/login -H "Content-Type: application/json" -d '{"username":"emp1","password":"pass"}'` → `token` 확보
4. **역할별 수동 시나리오:** 5개 토큰으로 각 액션 호출, 권한 매트릭스와 대조
5. **자동 테스트:** `./gradlew test` — M5의 통합 테스트 전부 통과 확인
6. **학습 체크포인트:** ExpensePolicy 메서드에 중단점 걸고 `@PreAuthorize` 평가 시점에 진입하는지, Specification이 조회 쿼리에 WHERE 절을 추가하는지 Hibernate 로그(`spring.jpa.show-sql=true`)로 확인

## 중요 파일 경로 (최종)

- [build.gradle](build.gradle)
- [SecurityConfig.java](src/main/java/com/example/abac/config/SecurityConfig.java)
- [ExpensePolicy.java](src/main/java/com/example/abac/policy/ExpensePolicy.java)
- [ExpenseSpecifications.java](src/main/java/com/example/abac/domain/expense/ExpenseSpecifications.java)
- [ExpenseController.java](src/main/java/com/example/abac/domain/expense/ExpenseController.java)
- [ExpenseIntegrationTest.java](src/test/java/com/example/abac/domain/expense/ExpenseIntegrationTest.java)
- [CLAUDE.md](CLAUDE.md)
