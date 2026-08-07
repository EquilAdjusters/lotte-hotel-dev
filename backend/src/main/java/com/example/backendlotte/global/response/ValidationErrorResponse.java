package com.example.backendlotte.global.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
    String code,
    String message,
    Map<String, String> errors,
    LocalDateTime timestamp
) {

    public static ValidationErrorResponse of(
            Map<String, String> errors
    ) {
        return new ValidationErrorResponse(
            "VALIDATION_FAILED",
            "입력값을 확인해주세요.",
            errors,
            LocalDateTime.now()
        );
    }
}