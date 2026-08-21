CREATE TABLE adjusting_company_hotel_companies (
    adjusting_company_id BIGINT NOT NULL,
    hotel_company_id BIGINT NOT NULL,

    PRIMARY KEY (adjusting_company_id, hotel_company_id),

    CONSTRAINT fk_acmc_adjusting_company
        FOREIGN KEY (adjusting_company_id)
        REFERENCES adjusting_companies (id),

    CONSTRAINT fk_acmc_hotel_company
        FOREIGN KEY (hotel_company_id)
        REFERENCES hotel_companies (id)
);

-- 기존에 이미 등록되어 있던 손사업체는 모든 호텔사에 노출되던 기존 동작을
-- 그대로 유지하도록 현재 존재하는 모든 호텔사와 연결해 둔다.
INSERT INTO adjusting_company_hotel_companies (adjusting_company_id, hotel_company_id)
SELECT ac.id, hc.id
FROM adjusting_companies ac
CROSS JOIN hotel_companies hc;
