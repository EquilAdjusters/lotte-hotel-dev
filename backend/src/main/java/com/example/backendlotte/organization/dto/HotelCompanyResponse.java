package com.example.backendlotte.organization.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.organization.entity.HotelCompany;

public record HotelCompanyResponse(
    Long id,
    String name,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static HotelCompanyResponse from(
            HotelCompany hotelCompany
    ) {
        return new HotelCompanyResponse(
            hotelCompany.getId(),
            hotelCompany.getName(),
            hotelCompany.isActive(),
            hotelCompany.getCreatedAt(),
            hotelCompany.getUpdatedAt()
        );
    }
}