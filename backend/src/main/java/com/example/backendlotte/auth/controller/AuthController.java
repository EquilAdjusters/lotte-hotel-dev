package com.example.backendlotte.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.auth.dto.LoginRequest;
import com.example.backendlotte.auth.dto.LoginResponse;
import com.example.backendlotte.auth.security.CustomUserDetails;
import com.example.backendlotte.auth.service.IpAccessService;
import com.example.backendlotte.auth.service.LoginAttemptService;
import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.type.LoginFailureReason;
import com.example.backendlotte.auth.service.IpAccessService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final AccountRepository accountRepository;
    private final LoginAttemptService loginAttemptService;
    private final IpAccessService ipAccessService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        Account account = accountRepository
            .findByLoginId(request.loginId())
            .orElse(null);

        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (account != null
                && !ipAccessService.isAllowed(account, ipAddress)) {

            loginAttemptService.recordFailure(
                account.getId(),
                request.loginId(),
                LoginFailureReason.IP_NOT_ALLOWED,
                ipAddress,
                userAgent
            );

            return ipNotAllowedResponse();
        }


        try {
            Authentication authentication =
                authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                        request.loginId(),
                        request.password()
                    )
                );

            // 기존 세션이 있다면 제거하고 새 세션 발급
            HttpSession existingSession =
                httpRequest.getSession(false);

            if (existingSession != null) {
                existingSession.invalidate();
            }

            HttpSession newSession = httpRequest.getSession(true);

            SecurityContext context =
                SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 인증 정보를 HTTP 세션에 저장
            securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
            );

            CustomUserDetails user =
                    (CustomUserDetails) authentication.getPrincipal();
                
            loginAttemptService.recordSuccess(
                    user.getAccountId(),
                    ipAddress,
                    userAgent,
                    newSession.getId()
            );

            return ResponseEntity.ok(
                new LoginResponse(
                    user.getAccountId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getRole(),
                    user.getScopeType()
                )
            );

        } catch (LockedException exception) {
            loginAttemptService.recordFailure(
                account == null ? null : account.getId(),
                request.loginId(),
                LoginFailureReason.ACCOUNT_LOCKED,
                ipAddress,
                userAgent
            );

            return lockedResponse();

        } catch (DisabledException exception) {
            loginAttemptService.recordFailure(
                account == null ? null : account.getId(),
                request.loginId(),
                LoginFailureReason.ACCOUNT_INACTIVE,
                ipAddress,
                userAgent
            );

            return inactiveResponse();
        } catch (AccountExpiredException exception) {

            loginAttemptService.recordFailure(
                account == null ? null : account.getId(),
                request.loginId(),
                LoginFailureReason.ACCOUNT_INACTIVE,
                ipAddress,
                userAgent
            );

            return inactiveResponse();
        } catch (BadCredentialsException exception) {
            LoginFailureReason reason = account == null
                ? LoginFailureReason.ACCOUNT_NOT_FOUND
                : LoginFailureReason.INVALID_PASSWORD;

            boolean locked = loginAttemptService.recordFailure(
                account == null ? null : account.getId(),
                request.loginId(),
                reason,
                ipAddress,
                userAgent
            );

            if (locked) {
                return lockedResponse();
            }

            return unauthorizedResponse();
        }
    }

    @GetMapping("/me")
    public LoginResponse me(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        return new LoginResponse(
                user.getAccountId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                user.getScopeType());
    }
    
    private ResponseEntity<Map<String, String>> unauthorizedResponse() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "code", "INVALID_CREDENTIALS",
                        "message", "아이디 또는 비밀번호가 올바르지 않습니다."));
    }
    
    private ResponseEntity<Map<String, String>> lockedResponse() {
        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(Map.of(
                        "code", "ACCOUNT_LOCKED",
                        "message", "비밀번호를 5회 이상 잘못 입력하여 계정이 잠겼습니다. 관리자에게 문의해주세요."));
    }
    
    private ResponseEntity<Map<String, String>> inactiveResponse() {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of(
                "code", "ACCOUNT_INACTIVE",
                "message", "사용이 중지된 계정입니다. 관리자에게 문의해주세요."
            ));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private ResponseEntity<Map<String, String>> ipNotAllowedResponse() {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(Map.of(
                "code", "IP_NOT_ALLOWED",
                "message", "허용되지 않은 접속 위치입니다. 관리자에게 문의해주세요."
            ));
    }
}