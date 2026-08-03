package com.example.backendlotte.account.type;

public enum AccountStatus {
    ACTIVE, // 정상 사용
    INACTIVE, // 사용 중지
    LOCKED, // 로그인 실패 등으로 잠김
    DELETED // 말소
}
