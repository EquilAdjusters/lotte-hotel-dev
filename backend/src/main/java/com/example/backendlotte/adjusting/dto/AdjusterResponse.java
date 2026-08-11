package com.example.backendlotte.adjusting.dto;

import com.example.backendlotte.adjusting.entity.Adjuster;

public record AdjusterResponse(
    Long id,
    Long adjustingCompanyId,
    String name,
    String phone
) {
    public static AdjusterResponse from(
            Adjuster adjuster
    ) {
        return new AdjusterResponse(
            adjuster.getId(),
            adjuster.getAdjustingCompany().getId(),
            adjuster.getName(),
            adjuster.getPhone()
        );
    }
}