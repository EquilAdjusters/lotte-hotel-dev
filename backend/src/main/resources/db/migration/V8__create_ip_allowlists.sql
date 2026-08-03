CREATE TABLE ip_allowlists (
    id BIGINT NOT NULL AUTO_INCREMENT,

    account_id BIGINT NULL,
    role VARCHAR(30) NULL,

    ip_address VARCHAR(50) NOT NULL,
    description VARCHAR(200) NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_ip_allowlists_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id),

    CONSTRAINT chk_ip_allowlists_target
        CHECK (account_id IS NOT NULL OR role IS NOT NULL)
);

CREATE INDEX idx_ip_allowlists_account_active
    ON ip_allowlists (account_id, active);

CREATE INDEX idx_ip_allowlists_role_active
    ON ip_allowlists (role, active);

CREATE UNIQUE INDEX uk_ip_allowlists_account_ip
    ON ip_allowlists (account_id, ip_address);

CREATE UNIQUE INDEX uk_ip_allowlists_role_ip
    ON ip_allowlists (role, ip_address);