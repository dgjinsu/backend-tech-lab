# 데이터 모델 설계 (ERD)

## 1. ER 다이어그램

```
┌─────────────┐       ┌─────────────────┐       ┌──────────────┐
│    users     │       │    salaries      │       │  categories  │
├─────────────┤       ├─────────────────┤       ├──────────────┤
│ PK id        │──┐   │ PK id            │       │ PK id         │
│ email        │  │   │ FK user_id       │──┐   │ FK user_id    │──┐
│ password     │  │   │ year             │  │   │ name          │  │
│ nickname     │  │   │ month            │  │   │ color         │  │
│ created_at   │  │   │ total_amount     │  │   │ icon          │  │
│ updated_at   │  │   │ memo             │  │   │ is_default    │  │
└─────────────┘  │   │ created_at       │  │   │ sort_order    │  │
                 │   │ updated_at       │  │   │ created_at    │  │
                 │   └─────────────────┘  │   │ updated_at    │  │
                 │                         │   └──────────────┘  │
                 │   ┌─────────────────┐  │                      │
                 │   │ fixed_expenses   │  │   ┌──────────────┐  │
                 │   ├─────────────────┤  │   │   expenses    │  │
                 │   │ PK id            │  │   ├──────────────┤  │
                 │   │ FK salary_id     │──┘   │ PK id         │  │
                 │   │ name             │      │ FK user_id    │──┤
                 │   │ amount           │      │ FK category_id│──┘
                 │   └─────────────────┘      │ amount        │
                 │                             │ description   │
                 │                             │ expense_date  │
                 │                             │ memo          │
                 │                             │ created_at    │
                 │   ┌─────────────────┐      │ updated_at    │
                 │   │    budgets       │      └──────────────┘
                 │   ├─────────────────┤
                 │   │ PK id            │
                 ├──▶│ FK user_id       │
                 │   │ FK category_id   │──────────────────────┐
                 │   │ year             │                      │
                 │   │ month            │                      │
                 │   │ amount           │                      │
                 │   │ created_at       │                      │
                 │   │ updated_at       │                      │
                 │   └─────────────────┘                      │
                 │                                             │
                 └─── users (1) ──▶ (*) salaries              │
                 └─── users (1) ──▶ (*) expenses              │
                 └─── users (1) ──▶ (*) categories            │
                 └─── users (1) ──▶ (*) budgets               │
                 categories (1) ──▶ (*) expenses ◀────────────┘
                 categories (1) ──▶ (*) budgets
                 salaries (1) ──▶ (*) fixed_expenses
```

---

## 2. 엔티티 상세 정의

### 2.1 User (사용자)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 사용자 고유 ID |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | 이메일 (로그인 ID) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt 해시 비밀번호 |
| `nickname` | VARCHAR(20) | NOT NULL | 닉네임 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 생성일시 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 수정일시 |

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
```

---

### 2.2 Salary (급여)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 급여 고유 ID |
| `user_id` | BIGINT | FK → users(id), NOT NULL | 사용자 ID |
| `year` | INT | NOT NULL | 연도 |
| `month` | INT | NOT NULL, CHECK(1~12) | 월 |
| `total_amount` | BIGINT | NOT NULL, CHECK(>= 0) | 총 급여액 (원) |
| `memo` | VARCHAR(200) | NULL | 메모 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 생성일시 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 수정일시 |

**유니크 제약**: `(user_id, year, month)` -- 사용자당 월별 1개의 급여만 허용

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "salaries",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_salary_user_year_month",
        columnNames = {"user_id", "year", "month"}
    ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Salary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(length = 200)
    private String memo;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedExpense> fixedExpenses = new ArrayList<>();

    @Builder
    public Salary(User user, Integer year, Integer month, Long totalAmount, String memo) {
        this.user = user;
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
        this.memo = memo;
    }

    // 가용 용돈 계산
    public Long getAvailableAmount() {
        long fixedTotal = fixedExpenses.stream()
            .mapToLong(FixedExpense::getAmount)
            .sum();
        return totalAmount - fixedTotal;
    }

    public void addFixedExpense(FixedExpense fixedExpense) {
        fixedExpenses.add(fixedExpense);
        fixedExpense.setSalary(this);
    }
}
```

---

### 2.3 FixedExpense (고정 지출)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 고정 지출 고유 ID |
| `salary_id` | BIGINT | FK → salaries(id), NOT NULL | 급여 ID |
| `name` | VARCHAR(50) | NOT NULL | 고정 지출 항목명 (예: 월세, 보험) |
| `amount` | BIGINT | NOT NULL, CHECK(>= 0) | 금액 (원) |

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "fixed_expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private Salary salary;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public FixedExpense(String name, Long amount) {
        this.name = name;
        this.amount = amount;
    }

    // 패키지 접근 제한 setter (Salary.addFixedExpense에서 사용)
    void setSalary(Salary salary) {
        this.salary = salary;
    }
}
```

---

### 2.4 Category (카테고리)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 카테고리 고유 ID |
| `user_id` | BIGINT | FK → users(id), NULL | 사용자 ID (NULL이면 기본 카테고리) |
| `name` | VARCHAR(30) | NOT NULL | 카테고리명 |
| `color` | VARCHAR(7) | NOT NULL, DEFAULT '#C9CBCF' | HEX 색상코드 |
| `icon` | VARCHAR(30) | NOT NULL, DEFAULT 'tag' | 아이콘 이름 |
| `is_default` | BOOLEAN | NOT NULL, DEFAULT false | 기본 카테고리 여부 |
| `sort_order` | INT | NOT NULL, DEFAULT 0 | 정렬 순서 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 생성일시 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 수정일시 |

**유니크 제약**: `(user_id, name)` -- 같은 사용자 내에서 카테고리명 중복 불가 (기본 카테고리는 user_id가 NULL이므로 별도 처리)

**기본 카테고리 시드 데이터**:

| name | color | icon | sort_order |
|------|-------|------|------------|
| 식비 | #FF6384 | utensils | 1 |
| 카페/음료 | #FFCE56 | coffee | 2 |
| 교통 | #36A2EB | bus | 3 |
| 쇼핑 | #4BC0C0 | shopping-bag | 4 |
| 문화/여가 | #9966FF | film | 5 |
| 구독 | #FF9F40 | repeat | 6 |
| 기타 | #C9CBCF | more-horizontal | 7 |

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // null이면 기본(시스템) 카테고리

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(nullable = false, length = 30)
    private String icon;

    @Column(nullable = false)
    private Boolean isDefault;

    @Column(nullable = false)
    private Integer sortOrder;

    @Builder
    public Category(User user, String name, String color, String icon,
                    Boolean isDefault, Integer sortOrder) {
        this.user = user;
        this.name = name;
        this.color = color != null ? color : "#C9CBCF";
        this.icon = icon != null ? icon : "tag";
        this.isDefault = isDefault != null ? isDefault : false;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }

    public void update(String name, String color, String icon) {
        if (!this.isDefault) {
            this.name = name;
        }
        this.color = color;
        this.icon = icon;
    }
}
```

---

### 2.5 Expense (지출)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 지출 고유 ID |
| `user_id` | BIGINT | FK → users(id), NOT NULL | 사용자 ID |
| `category_id` | BIGINT | FK → categories(id), NOT NULL | 카테고리 ID |
| `amount` | BIGINT | NOT NULL, CHECK(> 0) | 지출 금액 (원) |
| `description` | VARCHAR(100) | NOT NULL | 지출 설명 |
| `expense_date` | DATE | NOT NULL | 지출 일자 |
| `memo` | VARCHAR(200) | NULL | 메모 |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 생성일시 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 수정일시 |

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(length = 200)
    private String memo;

    @Builder
    public Expense(User user, Category category, Long amount,
                   String description, LocalDate expenseDate, String memo) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.memo = memo;
    }

    public void update(Category category, Long amount,
                       String description, LocalDate expenseDate, String memo) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.memo = memo;
    }
}
```

---

### 2.6 Budget (예산)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| `id` | BIGSERIAL | PK | 예산 고유 ID |
| `user_id` | BIGINT | FK → users(id), NOT NULL | 사용자 ID |
| `category_id` | BIGINT | FK → categories(id), NOT NULL | 카테고리 ID |
| `year` | INT | NOT NULL | 연도 |
| `month` | INT | NOT NULL, CHECK(1~12) | 월 |
| `amount` | BIGINT | NOT NULL, CHECK(>= 0) | 예산 금액 (원) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 생성일시 |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | 수정일시 |

**유니크 제약**: `(user_id, category_id, year, month)` -- 사용자의 카테고리별 월 예산은 1개만 허용

**JPA 엔티티 설계**:

```java
@Entity
@Table(name = "budgets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_budget_user_category_year_month",
        columnNames = {"user_id", "category_id", "year", "month"}
    ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public Budget(User user, Category category, Integer year, Integer month, Long amount) {
        this.user = user;
        this.category = category;
        this.year = year;
        this.month = month;
        this.amount = amount;
    }

    public void updateAmount(Long amount) {
        this.amount = amount;
    }
}
```

---

## 3. 엔티티 관계 요약

| 관계 | 타입 | 설명 |
|------|------|------|
| User → Salary | 1:N | 한 사용자가 여러 월의 급여를 가짐 |
| Salary → FixedExpense | 1:N | 하나의 급여에 여러 고정 지출이 포함됨 |
| User → Category | 1:N | 한 사용자가 여러 사용자 정의 카테고리를 생성 (기본 카테고리는 user_id = NULL) |
| User → Expense | 1:N | 한 사용자가 여러 지출을 기록 |
| Category → Expense | 1:N | 하나의 카테고리에 여러 지출이 속함 |
| User → Budget | 1:N | 한 사용자가 여러 카테고리별 예산을 설정 |
| Category → Budget | 1:N | 하나의 카테고리에 월별 예산이 설정됨 |

---

## 4. 인덱스 전략

### 4.1 Primary Key 인덱스 (자동 생성)

모든 테이블의 `id` 컬럼에 PK 인덱스가 자동 생성된다.

### 4.2 Unique 인덱스 (자동 생성)

| 테이블 | 인덱스 | 컬럼 |
|--------|--------|------|
| users | uk_users_email | `email` |
| salaries | uk_salary_user_year_month | `user_id, year, month` |
| budgets | uk_budget_user_category_year_month | `user_id, category_id, year, month` |

### 4.3 조회 성능용 인덱스 (수동 생성)

| 테이블 | 인덱스명 | 컬럼 | 용도 |
|--------|----------|------|------|
| expenses | idx_expense_user_date | `user_id, expense_date` | 사용자의 일자별 지출 조회 |
| expenses | idx_expense_user_category | `user_id, category_id` | 사용자의 카테고리별 지출 조회 |
| expenses | idx_expense_user_year_month | `user_id, expense_date` | 월별 지출 통계 쿼리 |
| categories | idx_category_user | `user_id` | 사용자별 카테고리 목록 조회 |
| budgets | idx_budget_user_year_month | `user_id, year, month` | 사용자의 월별 예산 조회 |
| salaries | idx_salary_user_year | `user_id, year` | 사용자의 연도별 급여 조회 |

---

## 5. Flyway 마이그레이션 SQL

### V1__create_user_table.sql

```sql
CREATE TABLE users (
    id          BIGSERIAL       PRIMARY KEY,
    email       VARCHAR(100)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    nickname    VARCHAR(20)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);
```

### V2__create_category_table.sql

```sql
CREATE TABLE categories (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(30)     NOT NULL,
    color       VARCHAR(7)      NOT NULL DEFAULT '#C9CBCF',
    icon        VARCHAR(30)     NOT NULL DEFAULT 'tag',
    is_default  BOOLEAN         NOT NULL DEFAULT false,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_category_user ON categories(user_id);

-- 기본 카테고리 시드 데이터 (user_id = NULL → 모든 사용자 공통)
INSERT INTO categories (user_id, name, color, icon, is_default, sort_order) VALUES
    (NULL, '식비',       '#FF6384', 'utensils',        true, 1),
    (NULL, '카페/음료',  '#FFCE56', 'coffee',          true, 2),
    (NULL, '교통',       '#36A2EB', 'bus',             true, 3),
    (NULL, '쇼핑',       '#4BC0C0', 'shopping-bag',    true, 4),
    (NULL, '문화/여가',  '#9966FF', 'film',            true, 5),
    (NULL, '구독',       '#FF9F40', 'repeat',          true, 6),
    (NULL, '기타',       '#C9CBCF', 'more-horizontal', true, 7);
```

### V3__create_salary_table.sql

```sql
CREATE TABLE salaries (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    year            INT             NOT NULL,
    month           INT             NOT NULL CHECK (month BETWEEN 1 AND 12),
    total_amount    BIGINT          NOT NULL CHECK (total_amount >= 0),
    memo            VARCHAR(200),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_salary_user_year_month UNIQUE (user_id, year, month)
);

CREATE INDEX idx_salary_user_year ON salaries(user_id, year);

CREATE TABLE fixed_expenses (
    id          BIGSERIAL       PRIMARY KEY,
    salary_id   BIGINT          NOT NULL REFERENCES salaries(id) ON DELETE CASCADE,
    name        VARCHAR(50)     NOT NULL,
    amount      BIGINT          NOT NULL CHECK (amount >= 0)
);
```

### V4__create_expense_table.sql

```sql
CREATE TABLE expenses (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     BIGINT          NOT NULL REFERENCES categories(id),
    amount          BIGINT          NOT NULL CHECK (amount > 0),
    description     VARCHAR(100)    NOT NULL,
    expense_date    DATE            NOT NULL,
    memo            VARCHAR(200),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_expense_user_date ON expenses(user_id, expense_date);
CREATE INDEX idx_expense_user_category ON expenses(user_id, category_id);
```

### V5__create_budget_table.sql

```sql
CREATE TABLE budgets (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     BIGINT          NOT NULL REFERENCES categories(id),
    year            INT             NOT NULL,
    month           INT             NOT NULL CHECK (month BETWEEN 1 AND 12),
    amount          BIGINT          NOT NULL CHECK (amount >= 0),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_budget_user_category_year_month UNIQUE (user_id, category_id, year, month)
);

CREATE INDEX idx_budget_user_year_month ON budgets(user_id, year, month);
```

---

## 6. 통계 쿼리 참고

### 6.1 카테고리별 월간 지출 합계

```sql
SELECT
    c.id AS category_id,
    c.name AS category_name,
    c.color,
    c.icon,
    COALESCE(SUM(e.amount), 0) AS total_amount,
    COUNT(e.id) AS expense_count
FROM categories c
LEFT JOIN expenses e ON e.category_id = c.id
    AND e.user_id = :userId
    AND EXTRACT(YEAR FROM e.expense_date) = :year
    AND EXTRACT(MONTH FROM e.expense_date) = :month
WHERE c.user_id IS NULL OR c.user_id = :userId
GROUP BY c.id, c.name, c.color, c.icon
ORDER BY total_amount DESC;
```

### 6.2 월별 지출 합계 (연간)

```sql
SELECT
    EXTRACT(MONTH FROM e.expense_date) AS month,
    SUM(e.amount) AS total_expense
FROM expenses e
WHERE e.user_id = :userId
    AND EXTRACT(YEAR FROM e.expense_date) = :year
GROUP BY EXTRACT(MONTH FROM e.expense_date)
ORDER BY month;
```

### 6.3 예산 대비 지출 현황

```sql
SELECT
    b.id AS budget_id,
    c.id AS category_id,
    c.name AS category_name,
    c.color,
    b.amount AS budget_amount,
    COALESCE(SUM(e.amount), 0) AS spent_amount,
    b.amount - COALESCE(SUM(e.amount), 0) AS remaining_amount
FROM budgets b
JOIN categories c ON c.id = b.category_id
LEFT JOIN expenses e ON e.category_id = b.category_id
    AND e.user_id = b.user_id
    AND EXTRACT(YEAR FROM e.expense_date) = b.year
    AND EXTRACT(MONTH FROM e.expense_date) = b.month
WHERE b.user_id = :userId
    AND b.year = :year
    AND b.month = :month
GROUP BY b.id, c.id, c.name, c.color, b.amount
ORDER BY c.sort_order;
```

---

## 7. 데이터 삭제 전략

| 시나리오 | 동작 |
|----------|------|
| 사용자 삭제 | CASCADE: 해당 사용자의 모든 데이터(급여, 지출, 카테고리, 예산) 삭제 |
| 카테고리 삭제 (사용자 정의) | 해당 카테고리의 지출 내역은 "기타" 카테고리로 이관 후 삭제 |
| 카테고리 삭제 (기본) | 삭제 불가 (비즈니스 규칙으로 차단) |
| 급여 삭제 | CASCADE: 해당 급여의 고정 지출 함께 삭제 |
| 지출 삭제 | 단순 삭제 (다른 테이블에 영향 없음) |
| 예산 삭제 | 단순 삭제 (다른 테이블에 영향 없음) |

---

## 8. 금액 처리 원칙

| 항목 | 결정 |
|------|------|
| 금액 단위 | **원(KRW)** -- 소수점 없이 정수로 저장 |
| DB 타입 | `BIGINT` -- Long형으로 충분 (최대 약 922경 원) |
| FE 표시 | 천 단위 콤마 + "원" 접미사 (예: `1,500,000원`) |
| 음수 허용 | 예산 잔여 금액만 음수 가능 (초과 시). 지출/급여 금액은 양수만 허용 |
