package com.example.backendlotte.adjusting.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdjustingCompanyCreateRequest(

    @NotBlank(message = "손해사정업체명은 필수입니다.")
    @Size(max = 100)
    String name,

    @Size(max = 30)
    String businessNumber,

    List<Long> hotelCompanyIds

) {
}