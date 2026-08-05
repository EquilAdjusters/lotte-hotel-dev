package com.example.backendlotte.account.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    private static final Pattern ENGLISH =
        Pattern.compile("[A-Za-z]");

    private static final Pattern NUMBER =
        Pattern.compile("[0-9]");

    private static final Pattern SPECIAL =
        Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    public void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "비밀번호는 8자 이상이어야 합니다."
            );
        }

        if (!ENGLISH.matcher(password).find()) {
            throw new IllegalArgumentException(
                "비밀번호에 영문자를 포함해야 합니다."
            );
        }

        if (!NUMBER.matcher(password).find()) {
            throw new IllegalArgumentException(
                "비밀번호에 숫자를 포함해야 합니다."
            );
        }

        if (!SPECIAL.matcher(password).find()) {
            throw new IllegalArgumentException(
                "비밀번호에 특수문자를 포함해야 합니다."
            );
        }
    }
}