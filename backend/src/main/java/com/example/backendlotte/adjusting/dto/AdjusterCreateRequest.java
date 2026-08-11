package com.example.backendlotte.adjusting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdjusterCreateRequest(

    @NotNull(message = "손해사정업체는 필수입니다.")
    Long adjustingCompanyId,

    @NotBlank(message = "담당자명은 필수입니다.")
    @Size(max = 100)
    String name,

    @NotBlank(message = "담당자 연락처는 필수입니다.")
    @Size(max = 30)
    String phone

) {
}