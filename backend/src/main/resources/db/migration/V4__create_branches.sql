CREATE TABLE branches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    hotel_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_branches_hotel_name
        UNIQUE (hotel_id, name),

    CONSTRAINT fk_branches_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels (id)
);