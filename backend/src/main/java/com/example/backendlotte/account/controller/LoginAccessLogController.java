package com.example.backendlotte.account.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backendlotte.account.dto.LoginAccessLogIntegrityResponse;
import com.example.backendlotte.account.service.LoginAccessLogIntegrityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/login-access-logs")
@RequiredArgsConstructor
public class LoginAccessLogController {

    private final LoginAccessLogIntegrityService loginAccessLogIntegrityService;

    @GetMapping("/integrity")
    @PreAuthorize("hasRole('ADMIN1')")
    public LoginAccessLogIntegrityResponse verifyIntegrity() {
        return loginAccessLogIntegrityService.verify();
    }
}
