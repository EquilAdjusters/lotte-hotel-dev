package com.example.backendlotte.account.type;

public enum ScopeType {
    ALL, // 전체 사고 조회
    ASSIGNED, // 배정된 사고만 조회
    HOTEL,  // 소속 호텔 전체 지점
    BRANCH, // 소속 지점만
    BRANCH_GROUP // 권역별 지정 지점
}
