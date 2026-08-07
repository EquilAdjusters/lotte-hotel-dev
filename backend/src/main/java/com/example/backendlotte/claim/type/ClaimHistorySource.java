package com.example.backendlotte.claim.type;

public enum ClaimHistorySource {
    USER,               // 사용자의 직접 작업
    SYSTEM,             // 우리 시스템 자동 처리
    EXTERNAL_ADAPTER    // 외부 손사업체 어댑터 처리
}