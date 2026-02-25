# 가계부 (Budget Tracker)

## 프로젝트 개요

월급/급여를 입력하고 지출을 카테고리별로 기록하여, 어떤 분야에 얼마나 소비하는지 시각적으로 분석할 수 있는 개인 가계부 서비스.

---

## 기술 스택

### Backend
| 기술 | 버전/설명 |
|------|-----------|
| Java | 17+ |
| Spring Boot | 3.x |
| Spring Data JPA | ORM |
| PostgreSQL | 주 데이터베이스 |
| Redis | 캐싱, 세션 관리 |
| Gradle | 빌드 도구 |
| Flyway | DB 마이그레이션 |
| Spring Security + JWT | 인증/인가 |
| Swagger (SpringDoc) | API 문서화 |

### Frontend
| 기술 | 설명 |
|------|------|
| React | UI 라이브러리 |
| Vite | 빌드 도구 |
| TypeScript | 타입 안정성 |
| Tailwind CSS | 스타일링 |
| React Router | 라우팅 |
| TanStack Query | 서버 상태 관리 |
| Zustand | 클라이언트 상태 관리 |
| Recharts | 차트/시각화 |
| Axios | HTTP 클라이언트 |

### Infra / DevOps
| 기술 | 설명 |
|------|------|
| Docker / Docker Compose | 로컬 개발 환경 |
| GitHub Actions | CI/CD (선택) |

---

## 추가 권장 기술 스택

| 기술 | 이유 |
|------|------|
| **Flyway** | DB 스키마 버전 관리. JPA ddl-auto 대신 안전한 마이그레이션 |
| **TanStack Query** | 서버 상태 캐싱, 자동 재요청, 로딩/에러 상태 관리 |
| **Zustand** | 가볍고 간결한 클라이언트 상태 관리 (Redux 대비 보일러플레이트 최소) |
| **Recharts** | React 친화적 차트 라이브러리. 지출 분석 시각화에 필수 |
| **SpringDoc (Swagger)** | API 문서 자동 생성. FE/BE 협업 시 계약 역할 |

---

## 핵심 기능

### 1. 사용자 인증
- 회원가입 / 로그인 (JWT 기반)
- 프로필 관리

### 2. 급여 관리
- 월별 총 급여 입력
- 고정 지출 (월세, 보험 등) 설정
- **가용 용돈 = 급여 - 고정지출** 자동 계산

### 3. 지출 기록
- 지출 항목 CRUD (금액, 카테고리, 날짜, 메모)
- 카테고리: 식비, 교통, 쇼핑, 문화/여가, 카페/음료, 구독, 기타
- 사용자 정의 카테고리 추가 가능

### 4. 지출 분석 (대시보드)
- 카테고리별 지출 비율 (파이 차트)
- 월별 지출 추이 (라인/바 차트)
- 예산 대비 지출 현황 (프로그레스 바)
- 전월 대비 증감 비교

### 5. 예산 설정
- 카테고리별 월 예산 설정
- 예산 초과 시 알림/경고

### 6. 레퍼런스 기능 (추후 확장)
- 정기 결제 자동 등록
- 지출 통계 리포트 (주간/월간)
- 저축 목표 설정 및 추적
- 가계부 데이터 내보내기 (CSV/Excel)

---

## 프로젝트 구조

```
claude-code-teams/
├── backend/                    # Spring Boot 프로젝트
│   ├── src/main/java/
│   │   └── com/budget/
│   │       ├── domain/         # 도메인별 패키지
│   │       │   ├── user/
│   │       │   ├── salary/
│   │       │   ├── expense/
│   │       │   ├── category/
│   │       │   └── budget/
│   │       ├── global/         # 공통 설정, 예외 처리
│   │       └── infra/          # Redis, 외부 연동
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/       # Flyway 마이그레이션
│   └── build.gradle
├── frontend/                   # React + Vite 프로젝트
│   ├── src/
│   │   ├── components/         # 공통 컴포넌트
│   │   ├── pages/              # 페이지 컴포넌트
│   │   ├── hooks/              # 커스텀 훅
│   │   ├── api/                # API 호출 함수
│   │   ├── stores/             # Zustand 스토어
│   │   ├── types/              # TypeScript 타입 정의
│   │   └── utils/              # 유틸리티
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml          # PostgreSQL, Redis
├── CLAUDE.md                   # Claude 에이전트 가이드
├── PROJECT.md                  # 이 문서
└── .claude/
    ├── settings.json           # Teams 설정
    └── agents/                 # 에이전트 정의
        ├── backend-developer.md
        ├── frontend-developer.md
        └── code-reviewer.md
```

---

## Claude Teams 에이전트 구성

### 팀 구성

| 에이전트 | 역할 | 담당 영역 |
|----------|------|-----------|
| **product-manager** | 기획/설계 | 기능 명세, API 설계, 화면 설계, 태스크 분배, 우선순위 결정 |
| **backend-developer** | BE 개발 | Spring Boot API, JPA 엔티티, 비즈니스 로직, DB 마이그레이션 |
| **backend-tester** | BE 테스트 | 단위/통합/API 테스트 작성, 실행, 품질 검증 |
| **frontend-developer** | FE 개발 | React 컴포넌트, 페이지, API 연동, 차트/시각화 |
| **code-reviewer** | 코드 리뷰 | 보안, 성능, 테스트 커버리지, 코드 품질 점검 |

### 팀 워크플로우

```
1. PM: 기능 명세 + API 명세 + 화면 설계
   ↓
2. 병렬 개발 (PM이 태스크 분배)
   ├── BE: 엔티티 → 서비스 → 컨트롤러
   ├── BE Tester: 단위 테스트 → 통합 테스트 → API 테스트
   └── FE: 타입 정의 → API 연동 → 컴포넌트 → 페이지
   ↓
3. 코드 리뷰 (Reviewer)
   ↓
4. PM: 수용 기준 검증 → 통합 테스트 및 수정
```

---

## 개발 순서 (로드맵)

### Phase 1: 프로젝트 초기 세팅
- [ ] BE: Spring Boot 프로젝트 생성 + Docker Compose (Postgres, Redis)
- [ ] FE: React + Vite + TypeScript + Tailwind 프로젝트 생성
- [ ] 공통: API 명세 문서 작성

### Phase 2: 사용자 인증
- [ ] BE: User 엔티티, JWT 인증 API
- [ ] FE: 로그인/회원가입 페이지

### Phase 3: 급여 & 지출 핵심 기능
- [ ] BE: Salary, Expense, Category CRUD API
- [ ] FE: 급여 입력, 지출 기록 페이지

### Phase 4: 대시보드 & 분석
- [ ] BE: 통계 조회 API (카테고리별, 월별)
- [ ] FE: 대시보드 페이지 (차트, 요약)

### Phase 5: 예산 & 알림
- [ ] BE: Budget 설정/조회 API
- [ ] FE: 예산 설정 UI, 초과 경고

### Phase 6: 고도화
- [ ] 정기 결제 자동 등록
- [ ] 리포트 생성/내보내기
- [ ] 저축 목표 추적
