CREATE TABLE branch_groups (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    CONSTRAINT uk_branch_groups_name UNIQUE (name)
);

CREATE TABLE branch_group_members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    branch_group_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_branch_group_members_group_branch
        UNIQUE (branch_group_id, branch_id),

    CONSTRAINT fk_branch_group_members_group
        FOREIGN KEY (branch_group_id)
        REFERENCES branch_groups (id),

    CONSTRAINT fk_branch_group_members_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches (id)
);