CREATE TABLE claim_attachments (
    id BIGINT NOT NULL AUTO_INCREMENT,

    claim_id BIGINT NOT NULL,
    uploaded_by_account_id BIGINT NOT NULL,

    attachment_type VARCHAR(30) NOT NULL,

    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,

    /*
     * S3 버킷 내부 객체 경로.
     * 예:
     * claims/2608-0001/uuid-consent.pdf
     */
    object_key VARCHAR(500) NOT NULL,

    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,

    /*
     * 실제 삭제 대신 논리 삭제.
     * 감사 추적과 파일 정리 작업에 필요하다.
     */
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME(6) NULL,
    deleted_by_account_id BIGINT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_claim_attachments_object_key
        UNIQUE (object_key),

    CONSTRAINT fk_claim_attachments_claim
        FOREIGN KEY (claim_id)
        REFERENCES claims (id),

    CONSTRAINT fk_claim_attachments_uploaded_by
        FOREIGN KEY (uploaded_by_account_id)
        REFERENCES accounts (id),

    CONSTRAINT fk_claim_attachments_deleted_by
        FOREIGN KEY (deleted_by_account_id)
        REFERENCES accounts (id),

    CONSTRAINT chk_claim_attachments_type
        CHECK (
            attachment_type IN (
                'CONSENT_FORM',
                'ACCIDENT_REPORT',
                'ACCIDENT_PHOTO',
                'DAMAGE_PHOTO',
                'RECEIPT',
                'OTHER'
            )
        ),

    CONSTRAINT chk_claim_attachments_file_size
        CHECK (
            file_size > 0
        ),

    CONSTRAINT chk_claim_attachments_deleted
        CHECK (
            (
                deleted = 0
                AND deleted_at IS NULL
                AND deleted_by_account_id IS NULL
            )
            OR
            (
                deleted = 1
                AND deleted_at IS NOT NULL
                AND deleted_by_account_id IS NOT NULL
            )
        )
);


CREATE INDEX idx_claim_attachments_claim
    ON claim_attachments (
        claim_id,
        deleted,
        created_at
    );


CREATE INDEX idx_claim_attachments_type
    ON claim_attachments (
        attachment_type
    );