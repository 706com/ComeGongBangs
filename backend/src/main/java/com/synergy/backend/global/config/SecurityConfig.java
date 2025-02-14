package com.synergy.backend.global.config;

import com.synergy.backend.global.security.OAuth2Service;
import com.synergy.backend.global.security.filter.JwtFilter;
import com.synergy.backend.global.security.filter.LoginFilter;
import com.synergy.backend.global.security.filter.OAuth2AuthenticationFailureHandler;
import com.synergy.backend.global.security.filter.OAuth2Filter;
import com.synergy.backend.global.security.jwt.service.BlackListTokenService;
import com.synergy.backend.global.security.jwt.service.RefreshTokenService;
import com.synergy.backend.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
    private final RedisTemplate<String,String> redisTemplate;

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
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000"); // 허용할 출처
        config.addAllowedOrigin("http://localhost:3001");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://www.comegongbang.kro.kr");
        config.addAllowedOrigin("https://www.comegongbang.kro.kr:60005");
        config.addAllowedOrigin("http://183.109.119.198:60005");
        config.addAllowedOrigin("https://183.109.119.198:60005");
        config.addAllowedOrigin("10.109.158.141");
        config.addAllowedOrigin("10.109.126.16");
        config.addAllowedOrigin("10.110.160.105");
        config.addAllowedOrigin("10.96.180.103");

        config.addAllowedOrigin("https://www.comegongbangs.kro.kr");
        config.addAllowedOrigin("https://comegongbangs.kro.kr");

        config.addAllowedMethod("*"); // 허용할 메서드 (GET, POST, PUT 등)
        config.addAllowedHeader("*"); // 허용할 헤더
        config.setAllowCredentials(true); // 자격 증명 허용
        config.addExposedHeader("Access-Control-Allow-Origin");
        config.addExposedHeader("Authorization"); // 노출할 헤더 추가

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
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

        http.logout((auth) ->
                        auth
                                .logoutUrl("/logout")   // 로그아웃 url
                                .deleteCookies("JToken", "RefreshToken", "JSESSIONID")    // 쿠키 삭제
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    String refreshToken = null;
                                    String accessToken = null;
                                    if (request.getCookies() == null) {
                                        return;
                                    }
                                    for (Cookie cookie : request.getCookies()) {
                                        if (cookie.getName().equals("JToken")) {
                                            accessToken = cookie.getValue();
                                        }
                                        if (cookie.getName().equals("RefreshToken")) {
                                            refreshToken = cookie.getValue();
                                        }
                                    }

                                    // 토큰 블랙리스트 전략 -> 로그아웃시, 블랙리스트로 지정하여, 보안성 강화
                                    if (accessToken != null) {

//                                blackListTokenRepository.save(new BlackListToken(accessToken));
                                        log.info("======AToken 블랙리스트 등록=====");
//                                        String blackAccess = accessToken;
//                                        blackListTokenService.save(accessToken);
                                        blackListTokenService.save(accessToken);
                                    }
                                    if (refreshToken != null) {
//                                blackListTokenRepository.save(new BlackListToken(refreshToken));
                                        log.info("======RToken 블랙리스트 등록=====");
//                                        String blackRefresh = refreshToken;
//                                        blackListTokenService.save(refreshToken);
                                        blackListTokenService.save(refreshToken);

                                        refreshTokenService.delete(refreshToken);   // refresh token 삭제
                                    }
                                    response.sendRedirect(frontRedirectUrl);
                                })
        );

        http.addFilterBefore(new JwtFilter(jwtUtil, refreshTokenService, blackListTokenService,frontRedirectUrl), LoginFilter.class);
        http.addFilterAt(
                new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), refreshTokenService),
                UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login((config) -> {
            config.successHandler(oAuth2AuthorizationSuccessHandler);
            config.failureHandler(oAuth2AuthenticationFailureHandler);
            config.userInfoEndpoint((endpoint) -> endpoint.userService(oAuth2Service));
        });
        return http.build();
    }
}
