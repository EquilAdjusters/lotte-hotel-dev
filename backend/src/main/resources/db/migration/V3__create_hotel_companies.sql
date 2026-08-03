CREATE TABLE hotel_companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_hotel_companies_name UNIQUE (name)
);

ALTER TABLE hotels
    ADD COLUMN hotel_company_id BIGINT NULL,
    ADD CONSTRAINT fk_hotels_hotel_company
        FOREIGN KEY (hotel_company_id)
        REFERENCES hotel_companies (id);