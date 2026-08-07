package com.example.backendlotte.account.dto;

import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(

    @NotBlank(message = "계정 표시명은 필수입니다.")
    @Size(
        max = 100,
        message = "계정 표시명은 100자 이하로 입력해주세요."
    )
    String displayName,

    @NotNull(message = "역할은 필수입니다.")
    Role role,

    @NotNull(message = "조회 범위는 필수입니다.")
    ScopeType scopeType,

    boolean sharedAccount,

    Long hotelCompanyId,
    Long hotelId,
    Long branchId,
    Long branchGroupId
) {
}