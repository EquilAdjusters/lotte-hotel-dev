package com.example.backendlotte.account.dto;

public record AccountPasswordResetRequest(
    String newPassword,
    String newPasswordConfirm
) {
}