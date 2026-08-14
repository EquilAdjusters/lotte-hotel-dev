package com.example.backendlotte.adjusting.dto;

import com.example.backendlotte.adjusting.entity.AdjustingCompany;

public record AdjustingCompanyResponse(
    Long id,
    String name,
    String businessNumber,
    boolean active
) {
    public static AdjustingCompanyResponse from(
            AdjustingCompany company
    ) {
        return new AdjustingCompanyResponse(
            company.getId(),
            company.getName(),
            company.getBusinessNumber(),
            company.isActive()
        );
    }
}
