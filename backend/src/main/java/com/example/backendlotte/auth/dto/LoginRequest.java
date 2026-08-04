package com.example.backendlotte.auth.dto;

public record LoginRequest (
    String loginId,
    String password
) {

}
