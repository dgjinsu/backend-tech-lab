# abac — RBAC + ABAC 경비 관리 학습 프로젝트

Spring Boot 3.5 / Java 21 / Spring Security 6 / JPA / H2 기반, RBAC + ABAC 권한 체계를 **결합**한 경비 승인 워크플로우 샘플입니다. 학습 목적.

## 빠른 시작

```bash
./gradlew bootRun     # 포트 8090
./gradlew test        # 통합 테스트 12개
```

시딩 계정 5개 (비밀번호 모두 `pass`):

| 계정 | Role | 부서 |
|---|---|---|
| `emp1` / `emp2` | EMPLOYEE | Engineering / Sales |
| `mgr1` | MANAGER | Engineering |
| `fin1` | FINANCE | Finance |
| `admin1` | ADMIN | Finance |

## 두 권한 축

| 축 | 무엇을 강제 | 어디서 |
|---|---|---|
| **RBAC + ABAC 액션 권한** | "이 요청을 수행할 수 있는가" | `@PreAuthorize` + `ExpensePolicy` Bean |
| **ABAC 데이터 스코프** | "이 요청이 볼 수 있는 행은 어디까지인가" | JPA `Specification` (`ExpenseSpecifications.visibleTo`) |

### 권한 매트릭스

| Action | EMPLOYEE | MANAGER | FINANCE | ADMIN |
|---|---|---|---|---|
| 작성 / 본인 DRAFT 수정·삭제·제출 | O | O | O | O |
| 승인 / 반려 | X | O (같은 부서 && ≤1,000,000) | X | O |
| 지급 | X | X | O (APPROVED만) | O |
| 조회 범위 | 본인 건 | 자기 부서 | APPROVED+ 전사 | 전체 |

## API 요약

| Method | Path | 설명 |
|---|---|---|
| POST | `/auth/login` | 로그인 → JWT |
| POST | `/expenses` | 지출 작성 (DRAFT) |
| PATCH | `/expenses/{id}` | DRAFT 수정 |
| DELETE | `/expenses/{id}` | DRAFT 삭제 |
| GET | `/expenses` | 페이지 조회 (Role별 스코프 자동) |
| GET | `/expenses/{id}` | 단건 조회 (스코프 밖은 404) |
| POST | `/expenses/{id}/submit` | 제출 (DRAFT → SUBMITTED) |
| POST | `/expenses/{id}/approve` | 승인 (SUBMITTED → APPROVED) |
| POST | `/expenses/{id}/reject` | 반려 (SUBMITTED → REJECTED) |
| POST | `/expenses/{id}/pay` | 지급 (APPROVED → PAID) |

## 예제 플로우

```bash
login() { curl -s -X POST localhost:8090/auth/login -H "Content-Type: application/json" \
  -d "{\"username\":\"$1\",\"password\":\"pass\"}" | python -c "import sys,json;print(json.load(sys.stdin)['token'])"; }

EMP1=$(login emp1); MGR1=$(login mgr1); FIN1=$(login fin1)

# 지출 생성
curl -s -X POST localhost:8090/expenses -H "Authorization: Bearer $EMP1" \
  -H "Content-Type: application/json" -d '{"amount":"50000","description":"lunch"}'

# 워크플로우
curl -s -X POST localhost:8090/expenses/1/submit  -H "Authorization: Bearer $EMP1"
curl -s -X POST localhost:8090/expenses/1/approve -H "Authorization: Bearer $MGR1"
curl -s -X POST localhost:8090/expenses/1/pay     -H "Authorization: Bearer $FIN1"
```

## 자세한 내용

- 도메인 전체 설명·권한 매트릭스·학습 포인트: [CLAUDE.md](CLAUDE.md)
- 실행 가능한 권한 명세: [ExpenseIntegrationTest.java](src/test/java/com/example/abac/domain/expense/ExpenseIntegrationTest.java)
- 정책 단일 진실 소스: [ExpensePolicy.java](src/main/java/com/example/abac/policy/ExpensePolicy.java)
- 데이터 스코프 predicate: [ExpenseSpecifications.java](src/main/java/com/example/abac/domain/expense/ExpenseSpecifications.java)
