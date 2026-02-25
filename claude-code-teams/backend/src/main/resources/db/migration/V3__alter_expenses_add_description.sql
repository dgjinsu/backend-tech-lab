-- description 컬럼 추가 (기존 데이터를 위해 기본값 설정)
ALTER TABLE expenses ADD COLUMN description VARCHAR(100) NOT NULL DEFAULT '';
-- date -> expense_date 컬럼명 변경
ALTER TABLE expenses RENAME COLUMN date TO expense_date;
-- 기본값 제거 (description)
ALTER TABLE expenses ALTER COLUMN description DROP DEFAULT;
