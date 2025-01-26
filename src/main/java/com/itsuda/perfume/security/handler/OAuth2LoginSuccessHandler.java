package com.itsuda.perfume.security.handler;

import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.dto.response.JwtTokenDto;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.security.CustomUserDetails;
import com.itsuda.perfume.util.CookieUtil;
import com.itsuda.perfume.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(userPrincipal.getId(), userPrincipal.getRole());
        log.info("Generated Tokens - Access: {}, Refresh: {}", 
        jwtTokenDto.getAccessToken().substring(0, 20), 
        jwtTokenDto.getRefreshToken().substring(0, 20));
        
        // DB 업데이트 전 현재 토큰 확인
        log.info("Previous DB Token: {}", 
        userRepository.findById(userPrincipal.getId())
            .map(user -> user.getRefreshToken() != null ? user.getRefreshToken().substring(0, 20) : "null")
            .orElse("user not found"));

        userRepository.updateRefreshToken(userPrincipal.getId(), jwtTokenDto.getRefreshToken());

        // 캐시 방지 헤더 추가
        // OAuth2 인증 응답이 캐시되는 것을 방지
        // 매 요청마다 새로운 JWT 토큰이 생성되도록 보장
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 프론트 앱뷰 나올때까지 시큐어쿠키가 아닌 일반 쿠키 사용 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//        CookieUtil.addSecureCookie(response, "refreshToken", jwtTokenDto.getRefreshToken(), jwtUtil.getWebRefreshTokenExpirationSecond());
        CookieUtil.addCookie(response, "refreshToken", jwtTokenDto.getRefreshToken());
        CookieUtil.addCookie(response, "accessToken", jwtTokenDto.getAccessToken());

        //
        log.info("Response Cookies Set - Access: {}, Refresh: {}", 
        jwtTokenDto.getAccessToken().substring(0, 20), 
        jwtTokenDto.getRefreshToken().substring(0, 20));

        if (userPrincipal.getRole() == ERole.GUEST) {
            response.sendRedirect("http://localhost:5173/sign-up");
        } else {
            response.sendRedirect("http://localhost:5173");
        }
    }
}