package com.example.backendlotte.claim.type;

public enum ClaimHistoryType {
    CREATED,            // 최초 접수 생성
    STATUS_CHANGED,     // 접수 → 진행중 등 상태 변경
    CLOSED,             // 종결 처리
    CONSENT_UPDATED,    // 개인정보 동의 정보 변경
    ASSIGNED,
    REASSIGNED,
    UPDATED,
    CANCELLED
}