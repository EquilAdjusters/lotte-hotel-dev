package com.example.backendlotte.account.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backendlotte.account.dto.AccountSearchCondition;
import com.example.backendlotte.account.repository.AccountSpecification;
import com.example.backendlotte.account.type.AccountStatus;
import com.example.backendlotte.account.dto.AccountCreateRequest;
import com.example.backendlotte.account.dto.AccountResponse;
import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.entity.AccountHistory;
import com.example.backendlotte.account.repository.AccountHistoryRepository;
import com.example.backendlotte.account.repository.AccountRepository;
import com.example.backendlotte.account.dto.AccountUpdateRequest;
import com.example.backendlotte.account.dto.AccountPasswordResetRequest;
import com.example.backendlotte.account.dto.MyPasswordChangeRequest;
import com.example.backendlotte.auth.service.AccountSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyValidator passwordPolicyValidator;
    private final AccountAffiliationResolver accountAffiliationResolver;
    private final AccountSessionService accountSessionService;
    

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
                    "로그인 아이디는 필수입니다.");
        }

        if (request.displayName() == null
                || request.displayName().isBlank()) {
            throw new IllegalArgumentException(
                    "계정 표시명은 필수입니다.");
        }

        if (request.role() == null) {
            throw new IllegalArgumentException(
                    "역할은 필수입니다.");
        }

        if (request.scopeType() == null) {
            throw new IllegalArgumentException(
                    "조회 범위는 필수입니다.");
        }

        passwordPolicyValidator.validate(request.password());
    }
    
    @Transactional(readOnly = true)
    public Page<AccountResponse> findAll(
            AccountSearchCondition condition,
            Pageable pageable
    ) {
        return accountRepository
                .findAll(
                        AccountSpecification.search(condition),
                        pageable)
                .map(AccountResponse::from);
    }
    
    @Transactional(readOnly = true)
    public AccountResponse findOne(Long accountId) {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "계정을 찾을 수 없습니다."));

        return AccountResponse.from(account);
    }
    
    @Transactional
    public AccountResponse updateAccount(
            Long accountId,
            AccountUpdateRequest request,
            Long actorAccountId,
            String actorIp
    ) {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "계정을 찾을 수 없습니다."));

        if (account.getStatus() == AccountStatus.DELETED) {
            throw new IllegalArgumentException(
                    "삭제된 계정은 수정할 수 없습니다.");
        }

        Account actorAccount = accountRepository
                .findById(actorAccountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "작업 계정을 찾을 수 없습니다."));

        validateUpdateRequest(request);

        AccountAffiliationResolver.ResolvedAffiliation affiliation = accountAffiliationResolver.resolve(request);

        String beforeValue = buildAccountSnapshot(account);

        account.updateProfile(
                request.displayName().trim(),
                request.role(),
                request.scopeType(),
                request.sharedAccount(),
                affiliation.hotelCompany(),
                affiliation.hotel(),
                affiliation.branch(),
                affiliation.branchGroup());

        String afterValue = buildAccountSnapshot(account);

        accountHistoryRepository.save(
                AccountHistory.updated(
                        account,
                        actorAccount,
                        beforeValue,
                        afterValue,
                        actorIp));

        return AccountResponse.from(account);
    }
    
    private void validateUpdateRequest(
            AccountUpdateRequest request
    ) {
        if (request.displayName() == null
                || request.displayName().isBlank()) {
            throw new IllegalArgumentException(
                    "계정 표시명은 필수입니다.");
        }

        if (request.displayName().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "계정 표시명은 100자 이하로 입력해주세요.");
        }

        if (request.role() == null) {
            throw new IllegalArgumentException(
                    "역할은 필수입니다.");
        }

        if (request.scopeType() == null) {
            throw new IllegalArgumentException(
                    "조회 범위는 필수입니다.");
        }
    }
    
    private String buildAccountSnapshot(Account account) {
        return """
                {
                "displayName": "%s",
                "role": "%s",
                "scopeType": "%s",
                "sharedAccount": %s,
                "hotelCompanyId": %s,
                "hotelId": %s,
                "branchId": %s,
                "branchGroupId": %s
                }
                """.formatted(
                account.getDisplayName(),
                account.getRole(),
                account.getScopeType(),
                account.isSharedAccount(),
                account.getHotelCompany() == null
                        ? "null"
                        : account.getHotelCompany().getId(),
                account.getHotel() == null
                        ? "null"
                        : account.getHotel().getId(),
                account.getBranch() == null
                        ? "null"
                        : account.getBranch().getId(),
                account.getBranchGroup() == null
                        ? "null"
                        : account.getBranchGroup().getId());
    }
    
    @Transactional
    public void changeMyPassword(
            Long accountId,
            MyPasswordChangeRequest request,
            String actorIp
    ) {
        Account account = findUsableAccount(accountId);

        validatePasswordConfirmation(
                request.newPassword(),
                request.newPasswordConfirm());

        if (request.currentPassword() == null
                || request.currentPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "현재 비밀번호는 필수입니다.");
        }

        if (!passwordEncoder.matches(
                request.currentPassword(),
                account.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "현재 비밀번호가 올바르지 않습니다.");
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                account.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "새 비밀번호는 현재 비밀번호와 다르게 설정해주세요.");
        }

        passwordPolicyValidator.validate(
                request.newPassword());

        account.changePassword(
                passwordEncoder.encode(request.newPassword()));

        accountHistoryRepository.save(
                AccountHistory.passwordChanged(
                        account,
                        account,
                        actorIp));

        accountSessionService.expireAllSessions(accountId);
    }

    @Transactional
    public void resetPassword(
            Long accountId,
            AccountPasswordResetRequest request,
            Long actorAccountId,
            String actorIp
    ) {
        Account account = findUsableAccount(accountId);

        Account actorAccount = accountRepository
            .findById(actorAccountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "작업 계정을 찾을 수 없습니다."
                )
            );

        validatePasswordConfirmation(
            request.newPassword(),
            request.newPasswordConfirm()
        );

        passwordPolicyValidator.validate(
            request.newPassword()
        );

        if (passwordEncoder.matches(
                request.newPassword(),
                account.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                "새 비밀번호는 기존 비밀번호와 다르게 설정해주세요."
            );
        }

        account.changePassword(
            passwordEncoder.encode(request.newPassword())
        );

        accountHistoryRepository.save(
            AccountHistory.passwordReset(
                account,
                actorAccount,
                actorIp
            )
        );

        accountSessionService.expireAllSessions(accountId);
    }
    
    private Account findUsableAccount(Long accountId) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "계정을 찾을 수 없습니다."
                )
            );

        if (account.getStatus() == AccountStatus.DELETED) {
            throw new IllegalArgumentException(
                "삭제된 계정은 사용할 수 없습니다."
            );
        }

        return account;
    }

    private void validatePasswordConfirmation(
            String password,
            String passwordConfirm
    ) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                "새 비밀번호는 필수입니다."
            );
        }

        if (passwordConfirm == null
                || passwordConfirm.isBlank()) {
            throw new IllegalArgumentException(
                "새 비밀번호 확인값은 필수입니다."
            );
        }

        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException(
                "새 비밀번호와 비밀번호 확인값이 일치하지 않습니다."
            );
        }
    }
}