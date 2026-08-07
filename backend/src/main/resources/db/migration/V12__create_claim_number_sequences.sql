CREATE TABLE claim_number_sequences (
    period CHAR(4) NOT NULL,
    last_sequence INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (period),

    CONSTRAINT chk_claim_number_sequences_value
        CHECK (last_sequence >= 0)
);