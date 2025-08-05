package com.synergy.backend.global.security.filter;

import com.synergy.backend.global.security.jwt.service.BlackListTokenService;
import com.synergy.backend.global.security.jwt.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final BlackListTokenService blackListTokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                if ("RefreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null) {
            log.info("Access Token 블랙리스트 등록");
            blackListTokenService.save(accessToken);
        }

        if (refreshToken != null) {
            log.info("Refresh Token 블랙리스트 등록 및 삭제");
            blackListTokenService.save(refreshToken);
            refreshTokenService.delete(refreshToken);
        }
    }
}