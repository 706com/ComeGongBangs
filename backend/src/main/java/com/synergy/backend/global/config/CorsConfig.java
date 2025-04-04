package com.synergy.backend.global.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 허용할 요청 출처 설정
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://www.comegongbang.kro.kr",
                "https://comegongbangs.kro.kr"
        ));

        // 허용할 요청 메서드 설정
        config.addAllowedMethod("*");
        
        // 허용할 요청 헤더 설정
        config.addAllowedHeader("*");
        
        // 자격 증명 허용
        config.setAllowCredentials(true);
        
        // 노출할 응답 헤더 설정
        config.addExposedHeader("Access-Control-Allow-Origin");
        config.addExposedHeader("Authorization");

        // 모든 경로 대한 CORS 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}