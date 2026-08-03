CREATE TABLE login_access_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,

    account_id BIGINT NULL,
    attempted_login_id VARCHAR(50) NOT NULL,

    success TINYINT(1) NOT NULL,
    failure_reason VARCHAR(50) NULL,

    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(500) NULL,
    session_id VARCHAR(128) NULL,

    login_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    logout_at DATETIME(6) NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_login_access_logs_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);

CREATE INDEX idx_login_access_logs_account_login_at
    ON login_access_logs (account_id, login_at);

CREATE INDEX idx_login_access_logs_login_id_login_at
    ON login_access_logs (attempted_login_id, login_at);

CREATE INDEX idx_login_access_logs_success_login_at
    ON login_access_logs (success, login_at);

CREATE INDEX idx_login_access_logs_session_id
    ON login_access_logs (session_id);