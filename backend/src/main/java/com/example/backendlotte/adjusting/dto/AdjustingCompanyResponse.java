package com.example.backendlotte.adjusting.dto;

import java.util.List;

import com.example.backendlotte.adjusting.entity.AdjustingCompany;
import com.example.backendlotte.organization.entity.HotelCompany;

public record AdjustingCompanyResponse(
    Long id,
    String name,
    String businessNumber,
    boolean active,
    List<Long> hotelCompanyIds
) {
    public static AdjustingCompanyResponse from(
            AdjustingCompany company
    ) {
        return new AdjustingCompanyResponse(
            company.getId(),
            company.getName(),
            company.getBusinessNumber(),
            company.isActive(),
            company.getHotelCompanies()
                .stream()
                .map(HotelCompany::getId)
                .toList()
        );
    }
}
