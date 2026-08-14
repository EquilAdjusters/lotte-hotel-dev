CREATE TABLE claim_export_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_id BIGINT NOT NULL,

    search_condition TEXT NULL,
    exported_count INT NOT NULL,

    created_at DATETIME(6)
        NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    KEY idx_claim_export_logs_account_created_at (
        account_id,
        created_at
    ),

    CONSTRAINT fk_claim_export_logs_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);