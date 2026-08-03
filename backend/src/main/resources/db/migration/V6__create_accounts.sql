CREATE TABLE accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,

    login_id VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,

    role VARCHAR(30) NOT NULL,
    scope_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    shared_account TINYINT(1) NOT NULL DEFAULT 0,

    hotel_company_id BIGINT NULL,
    hotel_id BIGINT NULL,
    branch_id BIGINT NULL,
    branch_group_id BIGINT NULL,

    failed_login_count INT NOT NULL DEFAULT 0,
    locked_at DATETIME(6) NULL,
    last_login_at DATETIME(6) NULL,
    password_changed_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_accounts_login_id
        UNIQUE (login_id),

    CONSTRAINT fk_accounts_hotel_company
        FOREIGN KEY (hotel_company_id)
        REFERENCES hotel_companies (id),

    CONSTRAINT fk_accounts_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels (id),

    CONSTRAINT fk_accounts_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches (id),

    CONSTRAINT fk_accounts_branch_group
        FOREIGN KEY (branch_group_id)
        REFERENCES branch_groups (id)
);

CREATE INDEX idx_accounts_role
    ON accounts (role);

CREATE INDEX idx_accounts_status
    ON accounts (status);

CREATE INDEX idx_accounts_branch_id
    ON accounts (branch_id);

CREATE INDEX idx_accounts_branch_group_id
    ON accounts (branch_group_id);