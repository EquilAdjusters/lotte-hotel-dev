CREATE TABLE claim_view_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    claim_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,

    viewed_at DATETIME(6)
        NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    KEY idx_claim_view_logs_claim_viewed_at (
        claim_id,
        viewed_at
    ),

    CONSTRAINT fk_claim_view_logs_claim
        FOREIGN KEY (claim_id)
        REFERENCES claims (id),

    CONSTRAINT fk_claim_view_logs_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
);
