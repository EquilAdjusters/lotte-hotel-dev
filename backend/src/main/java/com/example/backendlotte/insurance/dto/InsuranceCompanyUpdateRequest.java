package com.example.backendlotte.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InsuranceCompanyUpdateRequest(

    @NotBlank(message = "보험사명은 필수입니다.")
    @Size(max = 100)
    String name

) {
}