package com.example.backendlotte.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import com.example.backendlotte.account.type.Role;

public class RoleBasedConcurrentSessionStrategy extends ConcurrentSessionControlAuthenticationStrategy {
    public RoleBasedConcurrentSessionStrategy(
        SessionRegistry sessionRegistry
    ) {
        super(sessionRegistry);

        /*
         * false:
         * 새 로그인을 허용하고 기존 세션을 만료시킴
         *
         * true:
         * 기존 세션을 유지하고 새 로그인을 거부함
        */

        setExceptionIfMaximumExceeded(false);
    }

    @Override
    protected int getMaximumSessionsForThisUser (
        Authentication authentication
    ) {

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails user)) {
            return 1;
        }

        Role role = user.getRole();

        if (role == Role.BRANCH_SHARED || role == Role.ADMIN4) {
            return -1; // 무제한
        }

        return 1;
    }
}
