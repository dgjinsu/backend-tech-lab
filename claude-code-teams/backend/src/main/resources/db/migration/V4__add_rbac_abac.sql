-- =============================================
-- V4__add_rbac_abac.sql
-- RBAC(Role) + ABAC(Department 스코프) 하이브리드 권한 체계 도입
-- =============================================

-- 1) Departments 테이블 신설
CREATE TABLE departments (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL UNIQUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2) 기본 부서 시드
INSERT INTO departments (name) VALUES
    ('ENGINEERING'),
    ('SALES'),
    ('HR');

-- 3) users: role / department_id 컬럼 추가
ALTER TABLE users ADD COLUMN role          VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE';
ALTER TABLE users ADD COLUMN department_id BIGINT      REFERENCES departments(id);

-- 기존 유저를 기본 부서(ENGINEERING)로 매핑 후 NOT NULL 적용
UPDATE users
   SET department_id = (SELECT id FROM departments WHERE name = 'ENGINEERING')
 WHERE department_id IS NULL;

ALTER TABLE users ALTER COLUMN department_id SET NOT NULL;

-- 4) expenses: department_id 컬럼 추가 (작성 시점 스냅샷)
ALTER TABLE expenses ADD COLUMN department_id BIGINT REFERENCES departments(id);

UPDATE expenses e
   SET department_id = (SELECT u.department_id FROM users u WHERE u.id = e.user_id);

ALTER TABLE expenses ALTER COLUMN department_id SET NOT NULL;

-- 5) 인덱스
CREATE INDEX idx_users_department_id       ON users(department_id);
CREATE INDEX idx_expenses_department_id    ON expenses(department_id);
CREATE INDEX idx_expenses_department_date  ON expenses(department_id, expense_date);
