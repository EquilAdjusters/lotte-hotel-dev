package com.example.backendlotte.global.config;
import com.example.backendlotte.auth.security.ExpiredSessionFilter;
import com.example.backendlotte.auth.security.RoleBasedConcurrentSessionStrategy;
import com.example.backendlotte.auth.security.CustomUserDetailsService;
import com.example.backendlotte.auth.security.CustomAuthenticationEntryPoint;

import java.util.List;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            ExpiredSessionFilter expiredSessionFilter
    ) throws Exception {

        http
                .authenticationProvider(authenticationProvider)

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED)
                )

                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/health")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        
        http.addFilterBefore(
            expiredSessionFilter,
            AuthorizationFilter.class
        );

        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher>
            httpSessionEventPublisher() {

        return new ServletListenerRegistrationBean<>(
            new HttpSessionEventPublisher()
        );
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
            SessionRegistry sessionRegistry
    ) {
        RoleBasedConcurrentSessionStrategy concurrentStrategy =
            new RoleBasedConcurrentSessionStrategy(sessionRegistry);

        ChangeSessionIdAuthenticationStrategy fixationStrategy =
            new ChangeSessionIdAuthenticationStrategy();

        RegisterSessionAuthenticationStrategy registerStrategy =
            new RegisterSessionAuthenticationStrategy(sessionRegistry);

        return new CompositeSessionAuthenticationStrategy(
            List.of(
                concurrentStrategy,
                fixationStrategy,
                registerStrategy
            )
        );
    }
}
