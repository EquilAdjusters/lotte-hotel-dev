-- =========================================================
-- claims.status 에 CANCELLED 추가
-- =========================================================

ALTER TABLE claims
    DROP CHECK chk_claims_status;

ALTER TABLE claims
    ADD CONSTRAINT chk_claims_status
    CHECK (
        status IN (
            'RECEIVED',
            'IN_PROGRESS',
            'CLOSED',
            'CANCELLED'
        )
    );


-- =========================================================
-- claim_histories.previous_status 에 CANCELLED 추가
-- =========================================================

ALTER TABLE claim_histories
    DROP CHECK chk_claim_histories_previous_status;

ALTER TABLE claim_histories
    ADD CONSTRAINT chk_claim_histories_previous_status
    CHECK (
        previous_status IS NULL
        OR previous_status IN (
            'RECEIVED',
            'IN_PROGRESS',
            'CLOSED',
            'CANCELLED'
        )
    );


-- =========================================================
-- claim_histories.current_status 에 CANCELLED 추가
-- =========================================================

ALTER TABLE claim_histories
    DROP CHECK chk_claim_histories_current_status;

ALTER TABLE claim_histories
    ADD CONSTRAINT chk_claim_histories_current_status
    CHECK (
        current_status IS NULL
        OR current_status IN (
            'RECEIVED',
            'IN_PROGRESS',
            'CLOSED',
            'CANCELLED'
        )
    );


-- =========================================================
-- claim_histories.history_type 에 CANCELLED 추가
-- =========================================================

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
            'CANCELLED'
        )
    );

ALTER TABLE claims
    ADD CONSTRAINT chk_claims_cancelled
    CHECK (
        (
            status = 'CANCELLED'
            AND cancelled_at IS NOT NULL
        )
        OR
        (
            status <> 'CANCELLED'
            AND cancelled_at IS NULL
        )
    );