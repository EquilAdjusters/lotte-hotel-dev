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
            'CONSENT_UPDATED',
            'CANCELLED',
            'ASSIGNED',
            'ASSIGNMENT_CHANGED'
        )
    );