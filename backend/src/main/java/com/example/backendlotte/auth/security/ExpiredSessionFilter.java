package com.example.backendlotte.auth.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExpiredSessionFilter extends OncePerRequestFilter {

    private final SessionRegistry sessionRegistry;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            SessionInformation sessionInformation =
                sessionRegistry.getSessionInformation(session.getId());

            if (sessionInformation != null
                    && sessionInformation.isExpired()) {

                session.invalidate();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write("""
                    {
                      "code": "SESSION_EXPIRED_BY_NEW_LOGIN",
                      "message": "다른 기기에서 로그인되어 현재 세션이 종료되었습니다."
                    }
                    """);

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}