# 가계부 (Budget Tracker) - Claude 에이전트 가이드

## 프로젝트 개요
개인 가계부 서비스. 급여 입력 → 지출 기록 → 카테고리별 분석 대시보드 제공.
상세 내용은 `PROJECT.md` 참고.

---

## 기술 스택 요약

- **BE**: Java 17+, Spring Boot 3.x, JPA, PostgreSQL, Redis, Gradle
- **FE**: React, Vite, TypeScript, Tailwind CSS, Zustand, TanStack Query, Recharts

---

## 프로젝트 구조

```
backend/    → Spring Boot (도메인별 패키지: domain/{user,salary,expense,category,budget})
frontend/   → React + Vite (pages/, components/, hooks/, api/, stores/, types/)
```

---

## Backend 컨벤션

### 빌드 & 실행
```bash
cd backend
./gradlew build          # 빌드
./gradlew test           # 테스트
./gradlew bootRun        # 실행
```

### 코드 스타일
- 도메인별 패키지 구조: `com.budget.domain.{도메인명}`
- 계층: Controller → Service → Repository
- DTO는 `record` 사용, 요청/응답 분리 (XxxRequest, XxxResponse)
- 예외 처리: `global/exception` 패키지에 커스텀 예외 + GlobalExceptionHandler
- API 경로: `/api/v1/{resource}`
- 엔티티에 `@Builder`, `@Getter` 사용 (Lombok). `@Setter` 지양

### 테스트
- 단위 테스트: JUnit 5 + Mockito
- 통합 테스트: `@SpringBootTest` + `@Testcontainers`
- 테스트 메서드명: `메서드명_조건_기대결과` 패턴

### DB 마이그레이션
- Flyway 사용. `src/main/resources/db/migration/` 경로
- 파일명: `V{번호}__{설명}.sql` (예: `V1__create_user_table.sql`)

---

## Frontend 컨벤션

### 빌드 & 실행
```bash
cd frontend
npm install              # 의존성 설치
npm run dev              # 개발 서버
npm run build            # 빌드
npm run lint             # 린트
```

### 코드 스타일
- 함수형 컴포넌트 + Hooks만 사용
- 컴포넌트 파일: PascalCase (예: `ExpenseForm.tsx`)
- 훅 파일: camelCase, `use` 접두사 (예: `useExpenses.ts`)
- API 호출은 `api/` 디렉터리에 모듈별 분리
- 서버 상태: TanStack Query, 클라이언트 상태: Zustand
- 스타일: Tailwind 유틸리티 클래스 우선

### 테스트
- Vitest + React Testing Library
- 사용자 인터랙션 기반 테스트 작성

---

## Git 컨벤션

### 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 리팩터링
test: 테스트 추가/수정
docs: 문서 수정
chore: 빌드, 설정 변경
```

### 브랜치
- `main`: 안정 브랜치
- `feature/{기능명}`: 기능 개발
- `fix/{버그명}`: 버그 수정

---

## Docker 로컬 환경

```bash
docker-compose up -d     # PostgreSQL + Redis 실행
docker-compose down      # 종료
```

---

## 에이전트 팀 가이드

이 프로젝트는 Claude Teams를 사용하여 3명의 에이전트가 병렬로 작업합니다.

| 에이전트 | 파일 영역 | 역할 |
|----------|-----------|------|
| product-manager | 문서/명세 | 기능 기획, API 명세, 화면 설계, 태스크 분배 |
| backend-developer | `backend/src/main/**` | API, 엔티티, 서비스, 프로덕션 코드 |
| backend-tester | `backend/src/test/**` | 단위/통합/API 테스트 작성 및 실행 |
| frontend-developer | `frontend/**` | 컴포넌트, 페이지, API 연동, 차트 |
| code-reviewer | 전체 (읽기) | 보안, 성능, 품질 리뷰 |

### 작업 규칙
1. PM이 **기능 명세 + API 명세를 먼저 작성**
2. BE와 FE는 명세 기반으로 **병렬로 개발**
3. 각 에이전트는 **자기 영역의 파일만 수정**
4. 코드 리뷰어는 **양쪽 코드를 모두 검토** 후 피드백
5. PM은 코드를 직접 작성하지 않고 **기획/설계/조율**만 담당
