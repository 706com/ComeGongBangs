package com.synergy.backend.global.config;

import com.synergy.backend.global.security.OAuth2Service;
import com.synergy.backend.global.security.filter.CustomLogoutHandler;
import com.synergy.backend.global.security.filter.JwtFilter;
import com.synergy.backend.global.security.filter.LoginFilter;
import com.synergy.backend.global.security.filter.OAuth2AuthenticationFailureHandler;
import com.synergy.backend.global.security.filter.OAuth2Filter;
import com.synergy.backend.global.security.jwt.service.BlackListTokenService;
import com.synergy.backend.global.security.jwt.service.RefreshTokenService;
import com.synergy.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final OAuth2Filter oAuth2AuthorizationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2Service oAuth2Service;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;
    private final CustomLogoutHandler customLogoutHandler;

    @Value("${app.redirect-url}")
    private String frontRedirectUrl;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        http.authorizeHttpRequests((auth) ->
                auth
                        .requestMatchers("/test/user").hasRole("USER")
                        .requestMatchers("/test/admin").hasRole("ADMIN")
                        .requestMatchers("/present/give", "/present/take").authenticated()
                        .anyRequest().permitAll()   // 일시적 모두 허용
        );

        // JWT Filter 추가
        http.addFilterBefore(new JwtFilter(jwtUtil, refreshTokenService, blackListTokenService, frontRedirectUrl), LoginFilter.class);

        // LoginFilter 커스텀
        http.addFilterAt(
                new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), refreshTokenService),
                UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인
        http.oauth2Login((config) -> {
            config.successHandler(oAuth2AuthorizationSuccessHandler);
            config.failureHandler(oAuth2AuthenticationFailureHandler);
            config.userInfoEndpoint((endpoint) -> endpoint.userService(oAuth2Service));
        });

        // 로그아웃 처리
        http.logout((auth) ->
                auth
                        .logoutUrl("/logout")   //요청 url
                        .deleteCookies("JToken", "RefreshToken", "JSESSIONID") // 쿠키 삭제
                        .addLogoutHandler(customLogoutHandler) // 커스텀 핸들러 주입
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.sendRedirect(frontRedirectUrl);
                        }))
        );

        return http.build();
    }
}
