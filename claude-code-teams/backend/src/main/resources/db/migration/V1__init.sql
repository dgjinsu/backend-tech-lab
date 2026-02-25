-- =============================================
-- V1__init.sql - 초기 테이블 생성 및 기본 데이터
-- =============================================

-- Users 테이블
CREATE TABLE users (
    id          BIGSERIAL       PRIMARY KEY,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    nickname    VARCHAR(50)     NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Categories 테이블
CREATE TABLE categories (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL,
    type        VARCHAR(20)     NOT NULL DEFAULT 'DEFAULT',
    user_id     BIGINT          REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Salaries 테이블
CREATE TABLE salaries (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount          BIGINT          NOT NULL,
    year            INTEGER         NOT NULL,
    month           INTEGER         NOT NULL,
    fixed_expense   BIGINT          NOT NULL DEFAULT 0,
    memo            VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Expenses 테이블
CREATE TABLE expenses (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT          NOT NULL REFERENCES categories(id),
    amount      BIGINT          NOT NULL,
    date        DATE            NOT NULL,
    memo        VARCHAR(500),
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Budgets 테이블
CREATE TABLE budgets (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT          NOT NULL REFERENCES categories(id),
    amount      BIGINT          NOT NULL,
    year        INTEGER         NOT NULL,
    month       INTEGER         NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 인덱스
-- =============================================

-- Users
CREATE INDEX idx_users_email ON users(email);

-- Categories
CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_type ON categories(type);

-- Salaries
CREATE INDEX idx_salaries_user_id ON salaries(user_id);
CREATE UNIQUE INDEX idx_salaries_user_year_month ON salaries(user_id, year, month);

-- Expenses
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_category_id ON expenses(category_id);
CREATE INDEX idx_expenses_user_date ON expenses(user_id, date);

-- Budgets
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE UNIQUE INDEX idx_budgets_user_category_year_month ON budgets(user_id, category_id, year, month);

-- =============================================
-- 기본 카테고리 데이터
-- =============================================

INSERT INTO categories (name, type, user_id) VALUES
    ('식비', 'DEFAULT', NULL),
    ('교통', 'DEFAULT', NULL),
    ('쇼핑', 'DEFAULT', NULL),
    ('문화/여가', 'DEFAULT', NULL),
    ('카페/음료', 'DEFAULT', NULL),
    ('구독', 'DEFAULT', NULL),
    ('기타', 'DEFAULT', NULL);
