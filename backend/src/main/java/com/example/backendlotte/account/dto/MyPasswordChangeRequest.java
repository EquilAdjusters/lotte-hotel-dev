package com.example.backendlotte.account.dto;

public record MyPasswordChangeRequest(
    String currentPassword,
    String newPassword,
    String newPasswordConfirm
) {
}