-- 피쳐 플래그 테이블이 비어있을 때만 기본 데이터 삽입
INSERT INTO feature_flags (feature_name, enabled, description)
SELECT 'test1', false, '테스트 기능 1'
WHERE NOT EXISTS (SELECT 1 FROM feature_flags WHERE feature_name = 'test1');

INSERT INTO feature_flags (feature_name, enabled, description)
SELECT 'test2', true, '테스트 기능 2'
WHERE NOT EXISTS (SELECT 1 FROM feature_flags WHERE feature_name = 'test2');

INSERT INTO feature_flags (feature_name, enabled, description)
SELECT 'test3', false, '테스트 기능 3'
WHERE NOT EXISTS (SELECT 1 FROM feature_flags WHERE feature_name = 'test3');
