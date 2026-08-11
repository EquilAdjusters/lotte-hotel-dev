CREATE TABLE adjusting_companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    business_number VARCHAR(30) NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id)
);


CREATE TABLE adjusters (
    id BIGINT NOT NULL AUTO_INCREMENT,
    adjusting_company_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    KEY idx_adjusters_adjusting_company (
        adjusting_company_id
    ),

    CONSTRAINT fk_adjusters_adjusting_company
        FOREIGN KEY (adjusting_company_id)
        REFERENCES adjusting_companies (id)
);