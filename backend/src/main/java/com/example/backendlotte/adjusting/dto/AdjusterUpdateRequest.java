package com.example.backendlotte.adjusting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdjusterUpdateRequest(

    @NotBlank(message = "담당자명은 필수입니다.")
    @Size(max = 100)
    String name,

    @NotBlank(message = "담당자 연락처는 필수입니다.")
    @Size(max = 30)
    String phone

) {
}