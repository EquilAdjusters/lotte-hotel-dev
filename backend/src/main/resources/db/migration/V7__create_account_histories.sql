CREATE TABLE account_histories (
    id BIGINT NOT NULL AUTO_INCREMENT,

    account_id BIGINT NOT NULL,
    actor_account_id BIGINT NULL,

    action_type VARCHAR(50) NOT NULL,
    before_value TEXT NULL,
    after_value TEXT NULL,
    description VARCHAR(1000) NULL,
    actor_ip VARCHAR(45) NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT fk_account_histories_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id),

    CONSTRAINT fk_account_histories_actor
        FOREIGN KEY (actor_account_id)
        REFERENCES accounts (id)
);

CREATE INDEX idx_account_histories_account_created
    ON account_histories (account_id, created_at);

CREATE INDEX idx_account_histories_actor_created
    ON account_histories (actor_account_id, created_at);

CREATE INDEX idx_account_histories_action_type
    ON account_histories (action_type);