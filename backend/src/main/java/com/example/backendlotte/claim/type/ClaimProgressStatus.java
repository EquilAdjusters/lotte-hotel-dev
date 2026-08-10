package com.example.backendlotte.claim.type;

public enum ClaimProgressStatus {

    IN_PROGRESS,        // 진행중
    CLOSED_PAID,        // 종결(보험금 지급)
    CLOSED_EXEMPTED,    // 종결(면책)
    CANCELLED           // 취소
}