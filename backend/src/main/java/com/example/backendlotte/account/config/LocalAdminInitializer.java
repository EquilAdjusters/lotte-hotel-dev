package com.example.backendlotte.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backendlotte.account.entity.Account;
import com.example.backendlotte.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile("local")
@RequiredArgsConstructor
public class LocalAdminInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.initial-admin.login-id}")
    private String loginId;

    @Value("${app.initial-admin.password}")
    private String password;

    @Value("${app.initial-admin.display-name}")
    private String displayName;

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.existsByLoginId(loginId)) {
            return;
        }

        Account account = Account.createAdmin1(
            loginId,
            passwordEncoder.encode(password),
            displayName
        );

        accountRepository.save(account);
    }
}