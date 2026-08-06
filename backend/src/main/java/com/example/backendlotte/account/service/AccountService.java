package com.example.backendlotte.account.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.dto.AccountCreateRequest;
import com.example.backendlotte.account.dto.AccountResponse;
import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.entity.AccountHistory;
import com.example.backendlotte.account.repository.AccountHistoryRepository;
import com.example.backendlotte.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final AccountAffiliationResolver accountAffiliationResolver;
    

    @Transactional
    public AccountResponse createAccount(
            AccountCreateRequest request,
            Long actorAccountId,
            String actorIp
    ) {
        validateRequest(request);

        if (accountRepository.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException(
                "이미 사용 중인 로그인 아이디입니다."
            );
        }

        Account actorAccount = accountRepository
            .findById(actorAccountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "작업 계정을 찾을 수 없습니다."
                )
            );

        String passwordHash =
            passwordEncoder.encode(request.password());
        
        AccountAffiliationResolver.ResolvedAffiliation affiliation = accountAffiliationResolver.resolve(request);
        
        Account account = Account.create(
            request.loginId(),
            passwordHash,
            request.displayName(),
            request.role(),
            request.scopeType(),
            request.sharedAccount(),
            affiliation.hotelCompany(),
            affiliation.hotel(),
            affiliation.branch(),
            affiliation.branchGroup()
        );

        accountRepository.save(account);

        String afterValue = """
            {
              "loginId": "%s",
              "displayName": "%s",
              "role": "%s",
              "scopeType": "%s",
              "sharedAccount": %s
            }
            """.formatted(
                account.getLoginId(),
                account.getDisplayName(),
                account.getRole(),
                account.getScopeType(),
                account.isSharedAccount()
            );

        accountHistoryRepository.save(
            AccountHistory.created(
                account,
                actorAccount,
                afterValue,
                actorIp
            )
        );

        return AccountResponse.from(account);
    }

    private void validateRequest(AccountCreateRequest request) {
        if (request.loginId() == null
                || request.loginId().isBlank()) {
            throw new IllegalArgumentException(
                "로그인 아이디는 필수입니다."
            );
        }

        if (request.displayName() == null
                || request.displayName().isBlank()) {
            throw new IllegalArgumentException(
                "계정 표시명은 필수입니다."
            );
        }

        if (request.role() == null) {
            throw new IllegalArgumentException(
                "역할은 필수입니다."
            );
        }

        if (request.scopeType() == null) {
            throw new IllegalArgumentException(
                "조회 범위는 필수입니다."
            );
        }

        passwordPolicyValidator.validate(request.password());
    }
}