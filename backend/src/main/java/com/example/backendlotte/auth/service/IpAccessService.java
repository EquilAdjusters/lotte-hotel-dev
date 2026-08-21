package com.example.backendlotte.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.entity.IpAllowlist;
import com.example.backendlotte.account.repository.IpAllowlistRepository;
import com.example.backendlotte.account.type.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpAccessService {
    
    private final IpAllowlistRepository ipAllowlistRepository;

    @Value("${app.security.ip-restriction.enabled:true}")
    private boolean enabled;

    @Transactional(readOnly = true)
    public boolean isAllowed(Account account, String clientIp) {
        if (!enabled) {
            return true;
        }

        // IP 특정 대상: ADMIN1 · ADMIN2 · ADMIN3 (01 회원체계 권한표 기준)
        // BRANCH_SHARED · ADMIN4는 IP 불특정 · 모바일 접속 허용
        if (account.getRole() != Role.ADMIN1
                && account.getRole() != Role.ADMIN2
                && account.getRole() != Role.ADMIN3) {
            return true;
        }

        List<IpAllowlist> accountRules =
            ipAllowlistRepository.findAllByAccountIdAndActiveTrue(
                account.getId()
        );

        // 계정별 규칙이 있으면 역할별 규칙보다 우선
        if (!accountRules.isEmpty()) {
            return matchesAny(accountRules, clientIp);
        }

        List<IpAllowlist> roleRules = ipAllowlistRepository.findAllByRoleAndActiveTrue(
                account.getRole()
            );

        // IP 제한 대상 역할인데 허용 IP가 하나도 등록되어 있지 않으면 접근 차단
        return !roleRules.isEmpty()
                && matchesAny(roleRules, clientIp);
    }
    
    private boolean matchesAny (
        List<IpAllowlist> rules,
        String clientIp
    ) {
        return rules.stream()
                .anyMatch(rule -> matches(rule.getIpAddress(), clientIp));
    }

    private boolean matches(
        String allowedAddress,
        String clientIp
    ) {
        try {
            return new IpAddressMatcher(allowedAddress)
                .matches(clientIp);
        } catch (IllegalArgumentException exception) {
            // DB에 잘못된 IP 규칙이 저장되어 있으면 허용하지 않음
            return false;
        }
    }
}
