ALTER TABLE login_access_logs
    ADD COLUMN previous_hash VARCHAR(64) NULL,
    ADD COLUMN record_hash VARCHAR(64) NULL;

-- 이 기능 도입 이전에 쌓인 기록은 체인으로 검증할 수 없으므로
-- 별도 표식을 남기고 체인 검증 대상에서 제외한다.
UPDATE login_access_logs
SET record_hash = 'LEGACY-UNCHAINED'
WHERE record_hash IS NULL;

ALTER TABLE login_access_logs
    MODIFY COLUMN record_hash VARCHAR(64) NOT NULL;
