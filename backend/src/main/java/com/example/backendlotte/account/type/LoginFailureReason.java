package com.example.backendlotte.account.type;

public enum LoginFailureReason {
    ACCOUNT_NOT_FOUND,       // 존재하지 않는 아이디
    INVALID_PASSWORD,        // 비밀번호 불일치
    ACCOUNT_INACTIVE,        // 사용 중지 계정
    ACCOUNT_LOCKED,          // 잠긴 계정
    IP_NOT_ALLOWED,          // 허용되지 않은 IP
    SESSION_LIMIT_EXCEEDED,  // 동시접속 제한 초과
    UNKNOWN                  // 기타 오류
}