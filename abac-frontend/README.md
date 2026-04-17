# abac-frontend

React + Vite + TypeScript + Tailwind — [abac](../abac) 백엔드(RBAC + ABAC 경비 관리)의 **학습용 UI 콘솔**.

역할별로 보이는 데이터 스코프와 액션 버튼이 어떻게 달라지는지를 눈으로 확인하는 것이 이 프론트엔드의 목적.

## 빠른 시작

```bash
# 터미널 1 — 백엔드
cd ../abac
./gradlew bootRun    # :8090

# 터미널 2 — 프론트
npm install          # 최초 1회
npm run dev          # :5173 (점유 시 5174로 폴백)
```

브라우저에서 `http://localhost:5173` 접속 → 로그인 카드의 시딩 계정 버튼(emp1/emp2/mgr1/fin1/admin1) 클릭 → 로그인.

## 스택

| 영역 | 선택 |
|---|---|
| 번들러 | Vite 5 |
| 프레임워크 | React 18 + TypeScript |
| 라우팅 | react-router-dom v6 |
| HTTP | axios (토큰 자동 부착 + 401 자동 로그아웃 인터셉터) |
| 서버 상태 | @tanstack/react-query v5 |
| 인증 상태 | React Context + localStorage |
| 스타일 | Tailwind CSS 3 |

## 프로젝트 구조

```
src/
├── main.tsx                    (QueryClient + Router + AuthProvider)
├── App.tsx                     (라우팅 테이블)
├── index.css                   (Tailwind directives)
│
├── api/
│   ├── client.ts               (axios + interceptors)
│   ├── auth.ts                 (login)
│   └── expenses.ts             (list/get/create/update/delete + workflow 4종)
│
├── auth/
│   ├── AuthContext.tsx         (user/token 상태)
│   ├── useAuth.ts
│   └── ProtectedRoute.tsx      (미인증 시 /login 리다이렉트)
│
├── types/                      (Role, Expense, PageResponse 등)
│
├── pages/
│   ├── LoginPage.tsx
│   └── ExpensesPage.tsx
│
├── components/
│   ├── Navbar.tsx              (현재 유저 뱃지 + 로그아웃)
│   ├── ExpenseTable.tsx
│   ├── ExpenseForm.tsx         (Create/Update 공용)
│   ├── StatusBadge.tsx         (상태별 색상)
│   ├── WorkflowActions.tsx     (submit/approve/reject/pay 버튼 렌더)
│   └── ui/                     (Button, Input, Modal)
│
├── hooks/
│   ├── useExpenses.ts          (React Query: list)
│   └── useExpenseMutations.ts  (7종 mutation + invalidate)
│
└── lib/
    ├── permissions.ts          (ExpensePolicy 거울 — UI 힌트 전용)
    └── format.ts               (금액·날짜 포맷)
```

## 백엔드 연동

- **baseURL**: `/api` (axios) → **Vite proxy**가 `:8090`으로 포워딩 ([vite.config.ts](vite.config.ts))
- **인증**: 로그인 시 JWT 저장(`localStorage`), 이후 모든 요청에 `Authorization: Bearer <token>` 자동 부착 ([src/api/client.ts](src/api/client.ts))
- **401 처리**: 응답 인터셉터가 토큰 제거 + `/login` 리다이렉트
- **CORS**: 백엔드 SecurityConfig에서 `localhost:5173/5174` 허용

## 역할별로 보이는 것

같은 데이터라도 로그인한 역할에 따라 다음이 달라집니다.

| 역할 | 보이는 건 | 버튼 |
|---|---|---|
| EMPLOYEE | 본인 건만 | (본인 DRAFT) 수정·삭제·제출 |
| MANAGER | 자기 부서 전체 | 자기 부서 SUBMITTED + ≤1M → 승인·반려 |
| FINANCE | APPROVED 이상 전사 | APPROVED → 지급 |
| ADMIN | 전체 | 상태별 모든 액션 |

**권한 판정의 최종 방어선은 서버**. [src/lib/permissions.ts](src/lib/permissions.ts)는 UI 힌트용 복제본이며, 버튼을 숨겨도 우회 요청은 서버가 403/409로 거절한다.

## 검증 시나리오 (수동)

1. emp1 로그인 → 본인 건만 보임
2. 새 지출 작성 → 즉시 목록 반영 (React Query invalidation)
3. 본인 DRAFT → 제출 → 상태 SUBMITTED
4. 로그아웃 후 mgr1 로그인 → SUBMITTED 건에 승인·반려 버튼
5. 승인 → APPROVED
6. fin1 로그인 → APPROVED 건만 보이고 지급 버튼
7. 지급 → PAID
8. admin1 로그인 → 전체 건 + 상태별 모든 버튼
9. 1,500,000원 초과 건은 mgr1 승인 버튼 숨김 (한도)

## 스크립트

```bash
npm run dev       # Vite dev server + HMR
npm run build     # tsc -b + vite build
npm run preview   # 빌드 결과 로컬 서빙
npm run lint      # eslint
```
