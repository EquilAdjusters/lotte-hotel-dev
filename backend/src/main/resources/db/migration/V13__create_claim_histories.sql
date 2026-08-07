CREATE TABLE claim_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,

    claim_id BIGINT NOT NULL,

    /*
     * 사용자 작업이면 계정 ID 저장.
     * 시스템·외부 어댑터 작업이면 NULL 가능.
     */
    actor_account_id BIGINT NULL,

    history_type VARCHAR(30) NOT NULL,
    source_type VARCHAR(30) NOT NULL,

    previous_status VARCHAR(30) NULL,
    current_status VARCHAR(30) NULL,

    closing_result VARCHAR(30) NULL,

    description VARCHAR(500) NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_claim_histories_claim
        FOREIGN KEY (claim_id)
        REFERENCES claims (id),

    CONSTRAINT fk_claim_histories_actor_account
        FOREIGN KEY (actor_account_id)
        REFERENCES accounts (id),

    CONSTRAINT chk_claim_histories_type
        CHECK (
            history_type IN (
                'CREATED',
                'STATUS_CHANGED',
                'CLOSED',
                'CONSENT_UPDATED'
            )
        ),

    CONSTRAINT chk_claim_histories_source
        CHECK (
            source_type IN (
                'USER',
                'SYSTEM',
                'EXTERNAL_ADAPTER'
            )
        ),

    CONSTRAINT chk_claim_histories_previous_status
        CHECK (
            previous_status IS NULL
            OR previous_status IN (
                'RECEIVED',
                'IN_PROGRESS',
                'CLOSED'
            )
        ),

    CONSTRAINT chk_claim_histories_current_status
        CHECK (
            current_status IS NULL
            OR current_status IN (
                'RECEIVED',
                'IN_PROGRESS',
                'CLOSED'
            )
        ),

    CONSTRAINT chk_claim_histories_closing_result
        CHECK (
            closing_result IS NULL
            OR closing_result IN (
                'INSURANCE_PAID',
                'EXEMPTED'
            )
        )
);


CREATE INDEX idx_claim_histories_claim_created_at
    ON claim_histories (
        claim_id,
        created_at
    );


CREATE INDEX idx_claim_histories_actor_account
    ON claim_histories (
        actor_account_id
    );