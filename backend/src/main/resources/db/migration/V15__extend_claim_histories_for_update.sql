ALTER TABLE claim_histories
    ADD COLUMN previous_value TEXT NULL AFTER closing_result,
    ADD COLUMN current_value TEXT NULL AFTER previous_value;

ALTER TABLE claim_histories
    DROP CHECK chk_claim_histories_type;

ALTER TABLE claim_histories
    ADD CONSTRAINT chk_claim_histories_type
    CHECK (
        history_type IN (
            'CREATED',
            'UPDATED',
            'STATUS_CHANGED',
            'CLOSED',
            'CONSENT_UPDATED'
        )
    );