package com.example.backendlotte.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountPasswordResetRequest(

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(
        max = 100,
        message = "새 비밀번호는 100자 이하로 입력해주세요."
    )
    String newPassword,

    @NotBlank(message = "새 비밀번호 확인값은 필수입니다.")
    String newPasswordConfirm
) {
}