package com.example.backendlotte.account.dto;

import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.type.ScopeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Size(
        min = 4,
        max = 50,
        message = "로그인 아이디는 4자 이상 50자 이하로 입력해주세요."
    )
    @Pattern(
        regexp = "^[A-Za-z0-9._-]+$",
        message = "로그인 아이디는 영문, 숫자, 마침표, 밑줄, 하이픈만 사용할 수 있습니다."
    )
    String loginId,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(
        max = 100,
        message = "비밀번호는 100자 이하로 입력해주세요."
    )
    String password,

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