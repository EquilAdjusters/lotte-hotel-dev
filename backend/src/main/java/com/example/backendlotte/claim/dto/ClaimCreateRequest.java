package com.example.backendlotte.claim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backendlotte.claim.type.ClaimType;
import com.example.backendlotte.claim.type.PreferredLanguage;
import com.example.backendlotte.claim.type.VictimType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClaimCreateRequest(

    @NotBlank(message = "피해자명은 필수입니다.")
    @Size(
        max = 100,
        message = "피해자명은 100자 이하로 입력해주세요."
    )
    String victimName,

    @NotBlank(message = "피해자 연락처는 필수입니다.")
    @Size(
        max = 30,
        message = "피해자 연락처는 30자 이하로 입력해주세요."
    )
    @Pattern(
        regexp = "^[0-9+\\-\\s()]+$",
        message = "피해자 연락처 형식이 올바르지 않습니다."
    )
    String victimPhone,

    @NotNull(message = "피해자 생년월일은 필수입니다.")
    @Past(message = "피해자 생년월일은 과거 날짜여야 합니다.")
    LocalDate victimBirthDate,

    @NotNull(message = "내·외국인 구분은 필수입니다.")
    VictimType victimType,

    PreferredLanguage preferredLanguage,

    @NotBlank(message = "거주 시·도는 필수입니다.")
    @Size(
        max = 50,
        message = "거주 시·도는 50자 이하로 입력해주세요."
    )
    String residenceSido,

    @NotBlank(message = "거주 시·군·구는 필수입니다.")
    @Size(
        max = 50,
        message = "거주 시·군·구는 50자 이하로 입력해주세요."
    )
    String residenceSigungu,

    @Size(
        max = 200,
        message = "상세주소는 200자 이하로 입력해주세요."
    )
    String residenceDetail,

    @NotNull(message = "사고유형은 필수입니다.")
    ClaimType claimType,

    @NotNull(message = "사고일시는 필수입니다.")
    @PastOrPresent(message = "사고일시는 현재보다 미래일 수 없습니다.")
    LocalDateTime accidentAt,

    @NotBlank(message = "사고경위는 필수입니다.")
    @Size(
        max = 200,
        message = "사고경위는 200자 이하로 입력해주세요."
    )
    String accidentDescription,

    /*
     * 공유계정 실사용자 식별용.
     * Account.displayName을 쓰는 값이 아니라 실제 접수자 입력값이다.
     */
    @NotBlank(message = "접수자명은 필수입니다.")
    @Size(
        max = 100,
        message = "접수자명은 100자 이하로 입력해주세요."
    )
    String receivedByName,

    @NotBlank(message = "접수자 내선번호는 필수입니다.")
    @Size(
        max = 30,
        message = "접수자 내선번호는 30자 이하로 입력해주세요."
    )
    String receivedByExtension,

    @NotNull(message = "개인정보 동의 정보는 필수입니다.")
    @Valid
    ClaimConsentRequest consent

) {
}