ALTER TABLE claims
    ADD COLUMN adjusting_company_id BIGINT NULL,
    ADD COLUMN adjuster_id BIGINT NULL,
    ADD COLUMN assigned_by_account_id BIGINT NULL,
    ADD COLUMN assigned_at DATETIME(6) NULL;

ALTER TABLE claims
    ADD CONSTRAINT fk_claims_adjusting_company
        FOREIGN KEY (adjusting_company_id)
        REFERENCES adjusting_companies (id);

ALTER TABLE claims
    ADD CONSTRAINT fk_claims_adjuster
        FOREIGN KEY (adjuster_id)
        REFERENCES adjusters (id);

ALTER TABLE claims
    ADD CONSTRAINT fk_claims_assigned_by_account
        FOREIGN KEY (assigned_by_account_id)
        REFERENCES accounts (id);

CREATE INDEX idx_claims_adjusting_company
    ON claims (adjusting_company_id);

CREATE INDEX idx_claims_adjuster
    ON claims (adjuster_id);