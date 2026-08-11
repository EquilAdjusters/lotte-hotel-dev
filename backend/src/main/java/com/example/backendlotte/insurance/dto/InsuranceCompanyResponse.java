package com.example.backendlotte.insurance.dto;

import com.example.backendlotte.insurance.entity.InsuranceCompany;

public record InsuranceCompanyResponse(
    Long id,
    String name,
    boolean active
) {
    public static InsuranceCompanyResponse from(
            InsuranceCompany company
    ) {
        return new InsuranceCompanyResponse(
            company.getId(),
            company.getName(),
            company.isActive()
        );
    }
}