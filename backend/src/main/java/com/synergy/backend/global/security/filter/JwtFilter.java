package com.synergy.backend.global.security.filter;

import com.synergy.backend.domain.member.model.entity.Member;
import com.synergy.backend.global.security.CustomUserDetails;
import com.synergy.backend.global.security.jwt.service.BlackListTokenService;
import com.synergy.backend.global.security.jwt.service.RefreshTokenService;
import com.synergy.backend.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;
    private final String frontRedirectUrl;

    // JWT 필터를 적용하지 않을 경로
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/login");
//                || path.startsWith("/product/search");
//                || path.startsWith("/product/detail/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //쿠키를 통한 요청 받기
        String accessToken = getCookieValue(request,"JToken");
        String refreshToken = getCookieValue(request,"RefreshToken");

        // JToken을 받지 못했으면 다음 필터로 넘기기.
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

//        String token = accessToken;

        // AccessToken 만료 체크
        if (jwtUtil.isExpired(accessToken)) {
            log.info("[JWT] =====AT 만료=====");

            // 블랙리스트에 등록되어 있으면 불법 토큰으로 판단
            if (blackListTokenService.checkBlackList(accessToken, refreshToken)) {
                log.error("[JWT] =====블랙리스트 토큰 적발 및 삭제 처리=====");

                // 불법 쿠키 삭제
                deleteCookie(response,"JToken");
                deleteCookie(response,"RefreshToken");

                // 로그아웃 강제 유도
                sendLogoutPage(response);
                return;
            }

            // AccessToken 만료 + refreshToken이 없을 때
            if (refreshToken == null) {
                log.error("[JWT] =====RT 없음=====");

                sendLogoutPage(response);
                return;
            }

            String reIssuedAccessToken = refreshTokenService.reIssueAccessToken(refreshToken);
            // client의 refreshToken이 변조되었거나, 만료되었거나, 서버가 가지고있는 refreshToken과 다를 때
            if (reIssuedAccessToken == null) {
                log.error("[JWT] =====RT 이상 감지=====");
                sendLogoutPage(response);

                return;
            }

            // RT로 인한 AT 재갱신
            addCookie(response,"JToken",reIssuedAccessToken);

            // RTR 전략으로 인한 RT 또한 재갱신
            String reIssuedRefreshToken = refreshTokenService.reIssueRefreshToken(refreshToken); //RTR 적용
            addCookie(response,"RefreshToken",reIssuedRefreshToken);

            log.info("[JWT] =====AT,RT 재갱신 완료=====");
        }

        // 정상 토큰 및 만료시간 통과
        Long idx = jwtUtil.getIdx(accessToken);
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 인증-인가용 임시 멤버 객체 생성
        Member member = new Member(idx, username, role);

        // 직접 CustomDetails 객체로 변환
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // ContextHolder에 심어줌으로서, LoginFilter가 로그인 된 사용자라고 판명
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    // 쿠키 정보 획득
    private String getCookieValue(HttpServletRequest request,String name){
        if(request.getCookies()==null){
            return null;
        }
        for(Cookie cookie : request.getCookies()){
            if(name.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

    // 쿠키 추가
    private void addCookie(HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // 쿠키 삭제
    private void deleteCookie(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // 로그아웃 강제 유도
    private void sendLogoutPage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"redirectUrl\": \"" + frontRedirectUrl + "/logout\"}");
        out.flush();
    }
}
