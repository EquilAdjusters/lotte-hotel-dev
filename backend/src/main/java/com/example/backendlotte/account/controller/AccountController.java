package com.example.backendlotte.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.account.dto.AccountCreateRequest;
import com.example.backendlotte.account.dto.AccountResponse;
import com.example.backendlotte.account.service.AccountService;
import com.example.backendlotte.auth.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody AccountCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest httpRequest
    ) {
        AccountResponse response =
            accountService.createAccount(
                request,
                user.getAccountId(),
                getClientIp(httpRequest)
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor =
            request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}