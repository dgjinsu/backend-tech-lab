# API 명세서

## 1. 공통 사항

### 1.1 Base URL

```
http://localhost:8080/api/v1
```

### 1.2 공통 응답 형식

#### 성공 응답

```json
{
  "success": true,
  "data": { ... },
  "message": null
}
```

#### 에러 응답

```json
{
  "status": 400,
  "code": "C001",
  "message": "잘못된 입력 값입니다.",
  "timestamp": "2026-02-25T10:30:00"
}
```

### 1.3 인증 방식

- JWT Bearer Token
- 헤더: `Authorization: Bearer {accessToken}`
- 인증이 필요 없는 엔드포인트: `/api/v1/auth/**`

### 1.4 페이지네이션 공통 파라미터

| 파라미터 | 타입 | 기본값 | 설명 |
|----------|------|--------|------|
| `page` | int | 0 | 페이지 번호 (0-based) |
| `size` | int | 20 | 페이지 크기 |
| `sort` | string | - | 정렬 기준 (예: `createdAt,desc`) |

#### 페이지네이션 응답 형식

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "totalElements": 150,
    "totalPages": 8,
    "number": 0,
    "size": 20,
    "first": true,
    "last": false
  },
  "message": null
}
```

### 1.5 공통 에러 코드

| 코드 | HTTP 상태 | 설명 |
|------|-----------|------|
| C001 | 400 | 잘못된 입력 값 |
| C002 | 500 | 서버 내부 오류 |
| C003 | 404 | 대상을 찾을 수 없음 |

---

## 2. 인증 API

### 2.1 회원가입

```
POST /api/v1/auth/signup
```

**인증 필요**: 아니오

#### 요청 바디

```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "홍길동"
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `email` | string | O | 이메일 형식, 최대 100자 |
| `password` | string | O | 8자 이상, 영문+숫자+특수문자 포함 |
| `nickname` | string | O | 2~20자 |

#### 성공 응답 `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "createdAt": "2026-02-25T10:00:00"
  },
  "message": "회원가입이 완료되었습니다."
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 이메일 중복 | 409 | A004 | 이미 사용 중인 이메일입니다. |
| 유효성 검사 실패 | 400 | C001 | (필드별 메시지) |

---

### 2.2 로그인

```
POST /api/v1/auth/login
```

**인증 필요**: 아니오

#### 요청 바디

```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `email` | string | O | 이메일 형식 |
| `password` | string | O | 비어있지 않을 것 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "nickname": "홍길동"
    }
  },
  "message": null
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 이메일/비밀번호 불일치 | 401 | A001 | 이메일 또는 비밀번호가 올바르지 않습니다. |

---

### 2.3 토큰 갱신

```
POST /api/v1/auth/refresh
```

**인증 필요**: 아니오 (Refresh Token 사용)

#### 요청 바디

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `refreshToken` | string | O | 유효한 Refresh Token |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "message": null
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 만료된 Refresh Token | 401 | A002 | 만료된 토큰입니다. |
| 유효하지 않은 토큰 | 401 | A003 | 유효하지 않은 토큰입니다. |

---

## 3. 급여 API

### 3.1 급여 등록

```
POST /api/v1/salaries
```

**인증 필요**: 예

#### 요청 바디

```json
{
  "year": 2026,
  "month": 2,
  "totalAmount": 4000000,
  "fixedExpenses": [
    {
      "name": "월세",
      "amount": 600000
    },
    {
      "name": "국민연금",
      "amount": 200000
    },
    {
      "name": "건강보험",
      "amount": 150000
    }
  ],
  "memo": "2월 급여"
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `year` | int | O | 2000 이상 |
| `month` | int | O | 1~12 |
| `totalAmount` | long | O | 0 이상 |
| `fixedExpenses` | array | X | 고정 지출 목록 |
| `fixedExpenses[].name` | string | O | 1~50자 |
| `fixedExpenses[].amount` | long | O | 0 이상 |
| `memo` | string | X | 최대 200자 |

#### 성공 응답 `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "year": 2026,
    "month": 2,
    "totalAmount": 4000000,
    "fixedExpenseTotal": 950000,
    "availableAmount": 3050000,
    "fixedExpenses": [
      {
        "id": 1,
        "name": "월세",
        "amount": 600000
      },
      {
        "id": 2,
        "name": "국민연금",
        "amount": 200000
      },
      {
        "id": 3,
        "name": "건강보험",
        "amount": 150000
      }
    ],
    "memo": "2월 급여",
    "createdAt": "2026-02-25T10:00:00"
  },
  "message": "급여가 등록되었습니다."
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 해당 월 급여 중복 | 409 | S002 | 해당 월의 급여가 이미 존재합니다. |
| 유효성 검사 실패 | 400 | C001 | (필드별 메시지) |

---

### 3.2 급여 목록 조회

```
GET /api/v1/salaries?year=2026
```

**인증 필요**: 예

#### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `year` | int | X | 현재 연도 | 조회 연도 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "year": 2026,
      "month": 1,
      "totalAmount": 4000000,
      "fixedExpenseTotal": 950000,
      "availableAmount": 3050000,
      "memo": "1월 급여"
    },
    {
      "id": 2,
      "year": 2026,
      "month": 2,
      "totalAmount": 4000000,
      "fixedExpenseTotal": 950000,
      "availableAmount": 3050000,
      "memo": "2월 급여"
    }
  ],
  "message": null
}
```

---

### 3.3 급여 상세 조회

```
GET /api/v1/salaries/{salaryId}
```

**인증 필요**: 예

#### 경로 변수

| 변수 | 타입 | 설명 |
|------|------|------|
| `salaryId` | long | 급여 ID |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "year": 2026,
    "month": 2,
    "totalAmount": 4000000,
    "fixedExpenseTotal": 950000,
    "availableAmount": 3050000,
    "fixedExpenses": [
      {
        "id": 1,
        "name": "월세",
        "amount": 600000
      },
      {
        "id": 2,
        "name": "국민연금",
        "amount": 200000
      }
    ],
    "memo": "2월 급여",
    "createdAt": "2026-02-25T10:00:00",
    "updatedAt": "2026-02-25T10:00:00"
  },
  "message": null
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 급여 없음 | 404 | S001 | 급여 정보를 찾을 수 없습니다. |
| 타인의 급여 조회 | 403 | C004 | 접근 권한이 없습니다. |

---

### 3.4 급여 수정

```
PUT /api/v1/salaries/{salaryId}
```

**인증 필요**: 예

#### 요청 바디

```json
{
  "totalAmount": 4200000,
  "fixedExpenses": [
    {
      "name": "월세",
      "amount": 600000
    },
    {
      "name": "국민연금",
      "amount": 210000
    }
  ],
  "memo": "2월 급여 (수정)"
}
```

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "year": 2026,
    "month": 2,
    "totalAmount": 4200000,
    "fixedExpenseTotal": 810000,
    "availableAmount": 3390000,
    "fixedExpenses": [
      {
        "id": 1,
        "name": "월세",
        "amount": 600000
      },
      {
        "id": 4,
        "name": "국민연금",
        "amount": 210000
      }
    ],
    "memo": "2월 급여 (수정)",
    "updatedAt": "2026-02-25T11:00:00"
  },
  "message": "급여가 수정되었습니다."
}
```

---

### 3.5 급여 삭제

```
DELETE /api/v1/salaries/{salaryId}
```

**인증 필요**: 예

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": null,
  "message": "급여가 삭제되었습니다."
}
```

---

## 4. 지출 API

### 4.1 지출 등록

```
POST /api/v1/expenses
```

**인증 필요**: 예

#### 요청 바디

```json
{
  "categoryId": 1,
  "amount": 15000,
  "description": "점심 식사",
  "expenseDate": "2026-02-25",
  "memo": "회사 근처 식당"
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `categoryId` | long | O | 유효한 카테고리 ID |
| `amount` | long | O | 1 이상 |
| `description` | string | O | 1~100자 |
| `expenseDate` | string (date) | O | `yyyy-MM-dd` 형식 |
| `memo` | string | X | 최대 200자 |

#### 성공 응답 `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "category": {
      "id": 1,
      "name": "식비",
      "color": "#FF6384",
      "icon": "utensils"
    },
    "amount": 15000,
    "description": "점심 식사",
    "expenseDate": "2026-02-25",
    "memo": "회사 근처 식당",
    "createdAt": "2026-02-25T12:30:00"
  },
  "message": "지출이 등록되었습니다."
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 카테고리 없음 | 404 | CT001 | 카테고리를 찾을 수 없습니다. |
| 유효성 검사 실패 | 400 | C001 | (필드별 메시지) |

---

### 4.2 지출 목록 조회

```
GET /api/v1/expenses?year=2026&month=2&categoryId=1&page=0&size=20&sort=expenseDate,desc
```

**인증 필요**: 예

#### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `year` | int | X | 현재 연도 | 조회 연도 |
| `month` | int | X | 현재 월 | 조회 월 |
| `categoryId` | long | X | - | 카테고리 필터 |
| `startDate` | string | X | - | 시작일 (`yyyy-MM-dd`) |
| `endDate` | string | X | - | 종료일 (`yyyy-MM-dd`) |
| `page` | int | X | 0 | 페이지 번호 |
| `size` | int | X | 20 | 페이지 크기 |
| `sort` | string | X | `expenseDate,desc` | 정렬 기준 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "category": {
          "id": 1,
          "name": "식비",
          "color": "#FF6384",
          "icon": "utensils"
        },
        "amount": 15000,
        "description": "점심 식사",
        "expenseDate": "2026-02-25",
        "memo": "회사 근처 식당"
      },
      {
        "id": 2,
        "category": {
          "id": 3,
          "name": "교통",
          "color": "#36A2EB",
          "icon": "bus"
        },
        "amount": 1400,
        "description": "버스",
        "expenseDate": "2026-02-25",
        "memo": null
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "number": 0,
    "size": 20,
    "first": true,
    "last": false
  },
  "message": null
}
```

---

### 4.3 지출 상세 조회

```
GET /api/v1/expenses/{expenseId}
```

**인증 필요**: 예

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "category": {
      "id": 1,
      "name": "식비",
      "color": "#FF6384",
      "icon": "utensils"
    },
    "amount": 15000,
    "description": "점심 식사",
    "expenseDate": "2026-02-25",
    "memo": "회사 근처 식당",
    "createdAt": "2026-02-25T12:30:00",
    "updatedAt": "2026-02-25T12:30:00"
  },
  "message": null
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 지출 없음 | 404 | E001 | 지출 내역을 찾을 수 없습니다. |

---

### 4.4 지출 수정

```
PUT /api/v1/expenses/{expenseId}
```

**인증 필요**: 예

#### 요청 바디

```json
{
  "categoryId": 1,
  "amount": 18000,
  "description": "점심 식사 (수정)",
  "expenseDate": "2026-02-25",
  "memo": "회사 근처 식당 - 후식 포함"
}
```

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 1,
    "category": {
      "id": 1,
      "name": "식비",
      "color": "#FF6384",
      "icon": "utensils"
    },
    "amount": 18000,
    "description": "점심 식사 (수정)",
    "expenseDate": "2026-02-25",
    "memo": "회사 근처 식당 - 후식 포함",
    "updatedAt": "2026-02-25T13:00:00"
  },
  "message": "지출이 수정되었습니다."
}
```

---

### 4.5 지출 삭제

```
DELETE /api/v1/expenses/{expenseId}
```

**인증 필요**: 예

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": null,
  "message": "지출이 삭제되었습니다."
}
```

---

## 5. 카테고리 API

### 5.1 카테고리 목록 조회

```
GET /api/v1/categories
```

**인증 필요**: 예

#### 성공 응답 `200 OK`

기본 카테고리(시스템 제공)와 사용자 정의 카테고리를 모두 반환한다.

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "식비",
      "color": "#FF6384",
      "icon": "utensils",
      "isDefault": true,
      "sortOrder": 1
    },
    {
      "id": 2,
      "name": "카페/음료",
      "color": "#FFCE56",
      "icon": "coffee",
      "isDefault": true,
      "sortOrder": 2
    },
    {
      "id": 3,
      "name": "교통",
      "color": "#36A2EB",
      "icon": "bus",
      "isDefault": true,
      "sortOrder": 3
    },
    {
      "id": 4,
      "name": "쇼핑",
      "color": "#4BC0C0",
      "icon": "shopping-bag",
      "isDefault": true,
      "sortOrder": 4
    },
    {
      "id": 5,
      "name": "문화/여가",
      "color": "#9966FF",
      "icon": "film",
      "isDefault": true,
      "sortOrder": 5
    },
    {
      "id": 6,
      "name": "구독",
      "color": "#FF9F40",
      "icon": "repeat",
      "isDefault": true,
      "sortOrder": 6
    },
    {
      "id": 7,
      "name": "기타",
      "color": "#C9CBCF",
      "icon": "more-horizontal",
      "isDefault": true,
      "sortOrder": 7
    },
    {
      "id": 100,
      "name": "반려동물",
      "color": "#FF6B6B",
      "icon": "heart",
      "isDefault": false,
      "sortOrder": 8
    }
  ],
  "message": null
}
```

---

### 5.2 카테고리 생성

```
POST /api/v1/categories
```

**인증 필요**: 예

#### 요청 바디

```json
{
  "name": "반려동물",
  "color": "#FF6B6B",
  "icon": "heart"
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `name` | string | O | 1~30자, 사용자 내 중복 불가 |
| `color` | string | X | HEX 색상코드 (기본값: 시스템 지정) |
| `icon` | string | X | 아이콘 이름 (기본값: `"tag"`) |

#### 성공 응답 `201 Created`

```json
{
  "success": true,
  "data": {
    "id": 100,
    "name": "반려동물",
    "color": "#FF6B6B",
    "icon": "heart",
    "isDefault": false,
    "sortOrder": 8
  },
  "message": "카테고리가 생성되었습니다."
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 이름 중복 | 409 | CT002 | 이미 존재하는 카테고리입니다. |

---

### 5.3 카테고리 수정

```
PUT /api/v1/categories/{categoryId}
```

**인증 필요**: 예

기본 카테고리는 `color`, `icon`만 수정 가능하다. `name` 변경은 사용자 정의 카테고리만 가능하다.

#### 요청 바디

```json
{
  "name": "반려동물 용품",
  "color": "#E74C3C",
  "icon": "heart"
}
```

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "id": 100,
    "name": "반려동물 용품",
    "color": "#E74C3C",
    "icon": "heart",
    "isDefault": false,
    "sortOrder": 8
  },
  "message": "카테고리가 수정되었습니다."
}
```

---

### 5.4 카테고리 삭제

```
DELETE /api/v1/categories/{categoryId}
```

**인증 필요**: 예

기본 카테고리는 삭제할 수 없다. 사용자 정의 카테고리만 삭제 가능하며, 해당 카테고리에 연결된 지출 내역은 "기타" 카테고리로 자동 이관된다.

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": null,
  "message": "카테고리가 삭제되었습니다. 관련 지출은 '기타' 카테고리로 이동되었습니다."
}
```

#### 에러 응답

| 상황 | HTTP 상태 | 에러 코드 | 메시지 |
|------|-----------|-----------|--------|
| 기본 카테고리 삭제 시도 | 400 | CT003 | 기본 카테고리는 삭제할 수 없습니다. |
| 카테고리 없음 | 404 | CT001 | 카테고리를 찾을 수 없습니다. |

---

## 6. 예산 API

### 6.1 예산 설정 (생성/수정)

```
PUT /api/v1/budgets
```

**인증 필요**: 예

해당 월/카테고리에 예산이 이미 존재하면 수정(덮어쓰기), 없으면 생성한다 (Upsert).

#### 요청 바디

```json
{
  "year": 2026,
  "month": 2,
  "budgets": [
    {
      "categoryId": 1,
      "amount": 300000
    },
    {
      "categoryId": 2,
      "amount": 100000
    },
    {
      "categoryId": 3,
      "amount": 150000
    }
  ]
}
```

| 필드 | 타입 | 필수 | 유효성 검사 |
|------|------|------|-------------|
| `year` | int | O | 2000 이상 |
| `month` | int | O | 1~12 |
| `budgets` | array | O | 최소 1개 |
| `budgets[].categoryId` | long | O | 유효한 카테고리 ID |
| `budgets[].amount` | long | O | 0 이상 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 2,
    "totalBudget": 550000,
    "budgets": [
      {
        "id": 1,
        "category": {
          "id": 1,
          "name": "식비",
          "color": "#FF6384",
          "icon": "utensils"
        },
        "amount": 300000
      },
      {
        "id": 2,
        "category": {
          "id": 2,
          "name": "카페/음료",
          "color": "#FFCE56",
          "icon": "coffee"
        },
        "amount": 100000
      },
      {
        "id": 3,
        "category": {
          "id": 3,
          "name": "교통",
          "color": "#36A2EB",
          "icon": "bus"
        },
        "amount": 150000
      }
    ]
  },
  "message": "예산이 설정되었습니다."
}
```

---

### 6.2 월별 예산 조회

```
GET /api/v1/budgets?year=2026&month=2
```

**인증 필요**: 예

#### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `year` | int | X | 현재 연도 | 조회 연도 |
| `month` | int | X | 현재 월 | 조회 월 |

#### 성공 응답 `200 OK`

예산과 함께 해당 월의 실제 지출 금액, 잔여 금액, 소진율을 함께 반환한다.

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 2,
    "totalBudget": 550000,
    "totalSpent": 320000,
    "totalRemaining": 230000,
    "budgets": [
      {
        "id": 1,
        "category": {
          "id": 1,
          "name": "식비",
          "color": "#FF6384",
          "icon": "utensils"
        },
        "budgetAmount": 300000,
        "spentAmount": 210000,
        "remainingAmount": 90000,
        "usageRate": 70.0,
        "isExceeded": false
      },
      {
        "id": 2,
        "category": {
          "id": 2,
          "name": "카페/음료",
          "color": "#FFCE56",
          "icon": "coffee"
        },
        "budgetAmount": 100000,
        "spentAmount": 110000,
        "remainingAmount": -10000,
        "usageRate": 110.0,
        "isExceeded": true
      },
      {
        "id": 3,
        "category": {
          "id": 3,
          "name": "교통",
          "color": "#36A2EB",
          "icon": "bus"
        },
        "budgetAmount": 150000,
        "spentAmount": 0,
        "remainingAmount": 150000,
        "usageRate": 0.0,
        "isExceeded": false
      }
    ]
  },
  "message": null
}
```

---

### 6.3 예산 삭제

```
DELETE /api/v1/budgets/{budgetId}
```

**인증 필요**: 예

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": null,
  "message": "예산이 삭제되었습니다."
}
```

---

## 7. 통계 API

### 7.1 월별 지출 통계

```
GET /api/v1/statistics/monthly?year=2026
```

**인증 필요**: 예

#### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `year` | int | X | 현재 연도 | 조회 연도 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "monthlyData": [
      {
        "month": 1,
        "totalIncome": 4000000,
        "totalExpense": 2800000,
        "balance": 1200000
      },
      {
        "month": 2,
        "totalIncome": 4000000,
        "totalExpense": 1500000,
        "balance": 2500000
      }
    ],
    "yearTotalIncome": 8000000,
    "yearTotalExpense": 4300000,
    "yearBalance": 3700000,
    "averageMonthlyExpense": 2150000
  },
  "message": null
}
```

---

### 7.2 카테고리별 지출 통계

```
GET /api/v1/statistics/category?year=2026&month=2
```

**인증 필요**: 예

#### 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `year` | int | X | 현재 연도 | 조회 연도 |
| `month` | int | X | 현재 월 | 조회 월 |

#### 성공 응답 `200 OK`

```json
{
  "success": true,
  "data": {
    "year": 2026,
    "month": 2,
    "totalExpense": 1500000,
    "categories": [
      {
        "categoryId": 1,
        "categoryName": "식비",
        "color": "#FF6384",
        "icon": "utensils",
        "amount": 600000,
        "percentage": 40.0,
        "count": 25
      },
      {
        "categoryId": 3,
        "categoryName": "교통",
        "color": "#36A2EB",
        "icon": "bus",
        "amount": 350000,
        "percentage": 23.3,
        "count": 40
      },
      {
        "categoryId": 4,
        "categoryName": "쇼핑",
        "color": "#4BC0C0",
        "icon": "shopping-bag",
        "amount": 250000,
        "percentage": 16.7,
        "count": 5
      },
      {
        "categoryId": 2,
        "categoryName": "카페/음료",
        "color": "#FFCE56",
        "icon": "coffee",
        "amount": 150000,
        "percentage": 10.0,
        "count": 15
      },
      {
        "categoryId": 5,
        "categoryName": "문화/여가",
        "color": "#9966FF",
        "icon": "film",
        "amount": 100000,
        "percentage": 6.7,
        "count": 3
      },
      {
        "categoryId": 7,
        "categoryName": "기타",
        "color": "#C9CBCF",
        "icon": "more-horizontal",
        "amount": 50000,
        "percentage": 3.3,
        "count": 2
      }
    ],
    "previousMonth": {
      "totalExpense": 2800000,
      "changeAmount": -1300000,
      "changeRate": -46.4
    }
  },
  "message": null
}
```

---

## 8. API 엔드포인트 요약

| HTTP 메서드 | 경로 | 설명 | 인증 |
|-------------|------|------|------|
| `POST` | `/api/v1/auth/signup` | 회원가입 | X |
| `POST` | `/api/v1/auth/login` | 로그인 | X |
| `POST` | `/api/v1/auth/refresh` | 토큰 갱신 | X |
| `POST` | `/api/v1/salaries` | 급여 등록 | O |
| `GET` | `/api/v1/salaries` | 급여 목록 조회 | O |
| `GET` | `/api/v1/salaries/{id}` | 급여 상세 조회 | O |
| `PUT` | `/api/v1/salaries/{id}` | 급여 수정 | O |
| `DELETE` | `/api/v1/salaries/{id}` | 급여 삭제 | O |
| `POST` | `/api/v1/expenses` | 지출 등록 | O |
| `GET` | `/api/v1/expenses` | 지출 목록 조회 | O |
| `GET` | `/api/v1/expenses/{id}` | 지출 상세 조회 | O |
| `PUT` | `/api/v1/expenses/{id}` | 지출 수정 | O |
| `DELETE` | `/api/v1/expenses/{id}` | 지출 삭제 | O |
| `GET` | `/api/v1/categories` | 카테고리 목록 조회 | O |
| `POST` | `/api/v1/categories` | 카테고리 생성 | O |
| `PUT` | `/api/v1/categories/{id}` | 카테고리 수정 | O |
| `DELETE` | `/api/v1/categories/{id}` | 카테고리 삭제 | O |
| `PUT` | `/api/v1/budgets` | 예산 설정 (Upsert) | O |
| `GET` | `/api/v1/budgets` | 월별 예산 조회 | O |
| `DELETE` | `/api/v1/budgets/{id}` | 예산 삭제 | O |
| `GET` | `/api/v1/statistics/monthly` | 월별 지출 통계 | O |
| `GET` | `/api/v1/statistics/category` | 카테고리별 지출 통계 | O |
