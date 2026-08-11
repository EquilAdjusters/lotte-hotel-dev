package com.example.backendlotte.adjusting.dto;

import jakarta.validation.constraints.NotNull;

public record ClaimAssignRequest(

    @NotNull(message = "손해사정업체는 필수입니다.")
    Long adjustingCompanyId,

    @NotNull(message = "담당자는 필수입니다.")
    Long adjusterId

) {
}