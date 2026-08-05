package com.example.backendlotte.organization.dto;

import java.time.LocalDateTime;

import com.example.backendlotte.organization.entity.Branch;

public record BranchResponse(
    Long id,
    Long hotelId,
    String hotelName,
    Long hotelCompanyId,
    String hotelCompanyName,
    String name,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(
            branch.getId(),
            branch.getHotel().getId(),
            branch.getHotel().getName(),
            branch.getHotel().getHotelCompany().getId(),
            branch.getHotel().getHotelCompany().getName(),
            branch.getName(),
            branch.isActive(),
            branch.getCreatedAt(),
            branch.getUpdatedAt()
        );
    }
}