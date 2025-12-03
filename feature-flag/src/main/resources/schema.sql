-- 피쳐 플래그 테이블 생성
CREATE TABLE IF NOT EXISTS feature_flags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_name VARCHAR(100) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL,
    description VARCHAR(500)
);
