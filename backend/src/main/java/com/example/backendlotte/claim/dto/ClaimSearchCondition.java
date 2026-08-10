package com.example.backendlotte.claim.dto;

import java.time.LocalDate;

import com.example.backendlotte.claim.type.ClaimProgressStatus;

public record ClaimSearchCondition(

    // 접수일자
    LocalDate receivedFrom,
    LocalDate receivedTo,

    // 사고일자
    LocalDate accidentFrom,
    LocalDate accidentTo,

    // 진행현황
    ClaimProgressStatus progressStatus,

    // 접수자명
    String receivedByName,

    // 피해자명
    String victimName

) {
}