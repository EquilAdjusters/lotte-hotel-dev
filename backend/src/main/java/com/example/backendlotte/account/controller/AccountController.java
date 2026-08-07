package com.example.backendlotte.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.backendlotte.account.dto.AccountUpdateRequest;
import com.example.backendlotte.account.dto.AccountSearchCondition;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.type.Role;
import com.example.backendlotte.account.dto.AccountCreateRequest;
import com.example.backendlotte.account.dto.AccountResponse;
import com.example.backendlotte.account.service.AccountService;
import com.example.backendlotte.auth.security.CustomUserDetails;
import com.example.backendlotte.account.dto.AccountPasswordResetRequest;
import com.example.backendlotte.account.dto.MyPasswordChangeRequest;

import jakarta.servlet.http.HttpSession;
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
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN1')")
    public Page<AccountResponse> findAll(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Role role,

            @RequestParam(required = false)
            AccountStatus status,

            @RequestParam(required = false)
            Long hotelCompanyId,

            @RequestParam(required = false)
            Long hotelId,

            @RequestParam(required = false)
            Long branchId,

            @RequestParam(required = false)
            Long branchGroupId,

            @PageableDefault(
                size = 20,
                sort = "createdAt"
            )
            Pageable pageable
    ) {
        AccountSearchCondition condition = new AccountSearchCondition(
                keyword,
                role,
                status,
                hotelCompanyId,
                hotelId,
                branchId,
                branchGroupId);

        return accountService.findAll(
                condition,
                pageable);
    }
    
    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public AccountResponse findOne(
            @PathVariable Long accountId
    ) {
        return accountService.findOne(accountId);
    }

    @PatchMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN1')")
    public AccountResponse updateAccount(
            @PathVariable Long accountId,
            @RequestBody AccountUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest httpRequest
    ) {
        return accountService.updateAccount(
                accountId,
                request,
                user.getAccountId(),
                getClientIp(httpRequest));
    }
    
    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changeMyPassword(
            @RequestBody MyPasswordChangeRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest httpRequest
    ) {
        accountService.changeMyPassword(
                user.getAccountId(),
                request,
                getClientIp(httpRequest));

        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of(
                        "code", "PASSWORD_CHANGED",
                        "message", "비밀번호가 변경되었습니다. 다시 로그인해주세요."));
    }
    
    @PatchMapping("/{accountId}/password-reset")
    @PreAuthorize("hasRole('ADMIN1')")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable Long accountId,
            @RequestBody AccountPasswordResetRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest httpRequest
    ) {
        accountService.resetPassword(
            accountId,
            request,
            user.getAccountId(),
            getClientIp(httpRequest)
        );

        return ResponseEntity.ok(
            Map.of(
                "code", "PASSWORD_RESET",
                "message", "비밀번호가 초기화되었습니다."
            )
        );
    }
}