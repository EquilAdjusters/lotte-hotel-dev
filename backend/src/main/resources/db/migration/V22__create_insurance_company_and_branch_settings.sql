CREATE TABLE insurance_companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6)
        NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)
        NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uk_insurance_companies_name (name)
);

ALTER TABLE branches
    ADD COLUMN insurance_company_id BIGINT NULL,
    ADD COLUMN receipt_email VARCHAR(200) NULL;

ALTER TABLE branches
    ADD CONSTRAINT fk_branches_insurance_company
        FOREIGN KEY (insurance_company_id)
        REFERENCES insurance_companies (id);

CREATE INDEX idx_branches_insurance_company
    ON branches (insurance_company_id);