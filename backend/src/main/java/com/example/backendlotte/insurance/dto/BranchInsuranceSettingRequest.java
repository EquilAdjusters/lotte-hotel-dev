package com.example.backendlotte.insurance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BranchInsuranceSettingRequest(

    @NotNull(message = "보험사는 필수입니다.")
    Long insuranceCompanyId,

    @NotBlank(message = "접수메일은 필수입니다.")
    @Email(message = "접수메일 형식이 올바르지 않습니다.")
    @Size(
        max = 200,
        message = "접수메일은 200자 이하로 입력해주세요."
    )
    String receiptEmail

) {
}