-- =============================================
-- V2__add_fixed_expenses.sql - 고정 지출 테이블 생성 및 급여 테이블 수정
-- =============================================

-- salaries 테이블: amount → total_amount 으로 rename, fixed_expense 컬럼 삭제
ALTER TABLE salaries RENAME COLUMN amount TO total_amount;
ALTER TABLE salaries DROP COLUMN fixed_expense;

-- fixed_expenses 테이블 생성
CREATE TABLE fixed_expenses (
    id          BIGSERIAL       PRIMARY KEY,
    salary_id   BIGINT          NOT NULL REFERENCES salaries(id) ON DELETE CASCADE,
    name        VARCHAR(50)     NOT NULL,
    amount      BIGINT          NOT NULL CHECK (amount >= 0)
);

CREATE INDEX idx_fixed_expenses_salary_id ON fixed_expenses(salary_id);
