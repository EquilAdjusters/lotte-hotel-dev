CREATE TABLE notification_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,

    claim_id BIGINT NULL,

    notification_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,

    recipient VARCHAR(30) NOT NULL,
    message TEXT NOT NULL,

    provider VARCHAR(50) NULL,
    provider_message_id VARCHAR(255) NULL,

    failure_reason VARCHAR(1000) NULL,

    requested_at DATETIME(6) NOT NULL,
    sent_at DATETIME(6) NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_notification_logs_claim
        FOREIGN KEY (claim_id)
        REFERENCES claims (id),

    CONSTRAINT chk_notification_logs_type
        CHECK (
            notification_type IN (
                'CLAIM_RECEIVED'
            )
        ),

    CONSTRAINT chk_notification_logs_status
        CHECK (
            status IN (
                'PENDING',
                'SUCCESS',
                'FAILED'
            )
        )
);

CREATE INDEX idx_notification_logs_claim
    ON notification_logs (
        claim_id,
        created_at
    );

CREATE INDEX idx_notification_logs_status
    ON notification_logs (
        status,
        created_at
    );