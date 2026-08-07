CREATE TABLE claims (
    id BIGINT NOT NULL AUTO_INCREMENT,

    -- 시스템 표준 접수번호
    claim_number VARCHAR(30) NOT NULL,

    -- 접수 당시 조직 정보
    hotel_company_id BIGINT NOT NULL,
    hotel_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,

    -- 실제 접수에 사용된 계정
    created_by_account_id BIGINT NOT NULL,

    -- 피해자 정보
    victim_name VARCHAR(100) NOT NULL,
    victim_phone VARCHAR(30) NOT NULL,
    victim_birth_date DATE NOT NULL,

    victim_type VARCHAR(30) NOT NULL,
    preferred_language VARCHAR(30) NULL,

    residence_sido VARCHAR(50) NOT NULL,
    residence_sigungu VARCHAR(50) NOT NULL,
    residence_detail VARCHAR(200) NULL,

    -- 사고 정보
    claim_type VARCHAR(30) NOT NULL,
    accident_at DATETIME(6) NOT NULL,
    accident_description VARCHAR(200) NOT NULL,

    -- 접수자 정보 스냅샷
    received_by_name VARCHAR(100) NOT NULL,
    received_by_extension VARCHAR(30) NOT NULL,

    -- 우리 시스템의 표준 상태
    status VARCHAR(30) NOT NULL DEFAULT 'RECEIVED',
    closing_result VARCHAR(30) NULL,
    closed_at DATETIME(6) NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_claims_claim_number
        UNIQUE (claim_number),

    CONSTRAINT fk_claims_hotel_company
        FOREIGN KEY (hotel_company_id)
        REFERENCES hotel_companies (id),

    CONSTRAINT fk_claims_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels (id),

    CONSTRAINT fk_claims_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches (id),

    CONSTRAINT fk_claims_created_by_account
        FOREIGN KEY (created_by_account_id)
        REFERENCES accounts (id),

    CONSTRAINT chk_claims_victim_type
        CHECK (
            victim_type IN (
                'DOMESTIC',
                'FOREIGNER'
            )
        ),

    CONSTRAINT chk_claims_preferred_language
        CHECK (
            (
                victim_type = 'DOMESTIC'
                AND preferred_language IS NULL
            )
            OR
            (
                victim_type = 'FOREIGNER'
                AND preferred_language IN (
                    'KOREAN',
                    'ENGLISH',
                    'CHINESE',
                    'JAPANESE'
                )
            )
        ),

    CONSTRAINT chk_claims_claim_type
        CHECK (
            claim_type IN (
                'PROPERTY_DAMAGE',
                'LIABILITY'
            )
        ),

    CONSTRAINT chk_claims_status
        CHECK (
            status IN (
                'RECEIVED',
                'IN_PROGRESS',
                'CLOSED'
            )
        ),

    CONSTRAINT chk_claims_closing
        CHECK (
            (
                status <> 'CLOSED'
                AND closing_result IS NULL
                AND closed_at IS NULL
            )
            OR
            (
                status = 'CLOSED'
                AND closing_result IN (
                    'INSURANCE_PAID',
                    'EXEMPTED'
                )
                AND closed_at IS NOT NULL
            )
        )
);


CREATE TABLE claim_consents (
    id BIGINT NOT NULL AUTO_INCREMENT,

    claim_id BIGINT NOT NULL,

    consent_status VARCHAR(30) NOT NULL,
    consent_obtained_at DATETIME(6) NULL,
    consent_method VARCHAR(30) NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_claim_consents_claim
        UNIQUE (claim_id),

    CONSTRAINT fk_claim_consents_claim
        FOREIGN KEY (claim_id)
        REFERENCES claims (id),

    CONSTRAINT chk_claim_consents_status
        CHECK (
            consent_status IN (
                'OBTAINED',
                'NOT_OBTAINED'
            )
        ),

    CONSTRAINT chk_claim_consents_values
        CHECK (
            (
                consent_status = 'OBTAINED'
                AND consent_obtained_at IS NOT NULL
                AND consent_method IN (
                    'WRITTEN',
                    'TEXT_MESSAGE',
                    'ORAL'
                )
            )
            OR
            (
                consent_status = 'NOT_OBTAINED'
                AND consent_obtained_at IS NULL
                AND consent_method IS NULL
            )
        )
);


CREATE INDEX idx_claims_victim_duplicate
    ON claims (
        victim_birth_date,
        victim_name
    );

CREATE INDEX idx_claims_branch_status
    ON claims (
        branch_id,
        status
    );

CREATE INDEX idx_claims_hotel_company_status
    ON claims (
        hotel_company_id,
        status
    );

CREATE INDEX idx_claims_accident_at
    ON claims (
        accident_at
    );