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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokens(userPrincipal.getId(), userPrincipal.getRole());
        userRepository.updateRefreshToken(userPrincipal.getId(), jwtTokenDto.getRefreshToken());

        // 프론트 앱뷰 나올때까지 시큐어쿠키가 아닌 일반 쿠키 사용 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//        CookieUtil.addSecureCookie(response, "refreshToken", jwtTokenDto.getRefreshToken(), jwtUtil.getWebRefreshTokenExpirationSecond());
        CookieUtil.addCookie(response, "refreshToken", jwtTokenDto.getRefreshToken());
        CookieUtil.addCookie(response, "accessToken", jwtTokenDto.getAccessToken());

        if (userPrincipal.getRole() == ERole.GUEST) {
            response.sendRedirect("http://localhost:5173/sign-up");
        } else {
            response.sendRedirect("http://localhost:5173");
        }
    }
}