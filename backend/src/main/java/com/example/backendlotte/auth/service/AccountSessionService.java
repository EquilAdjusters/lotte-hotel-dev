package com.example.backendlotte.auth.service;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.example.backendlotte.auth.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountSessionService {

    private final SessionRegistry sessionRegistry;

    public void expireAllSessions(Long accountId) {
        if (accountId == null) {
            return;
        }

        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (!(principal instanceof CustomUserDetails user)) {
                continue;
            }

            if (!accountId.equals(user.getAccountId())) {
                continue;
            }

            for (SessionInformation session :
                    sessionRegistry.getAllSessions(
                        principal,
                        false
                    )) {

                session.expireNow();
            }
        }
    }
}