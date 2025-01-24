package com.itsuda.perfume.security;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Spring Security의 기본 폼 로그인을 위한 사용자 정보 로드 메소드
     * username/password 방식의 인증에서 Spring Security가 자동으로 호출
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info(username);
        UserRepository.UserSecurityForm user = userRepository.findUserIdAndRoleBySerialId(username)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.create(user);
    }

    /**
     * JWT 토큰 인증을 위한 사용자 정보 로드 메소드
     * RefreshToken이 존재하는 사용자만 조회 가능
     * 토큰 기반 인증 시 수동으로 호출하여 사용
     */
    public UserDetails loadUserById(Long userId) throws RestApiException {
        UserRepository.UserSecurityForm user = userRepository.findByIdAndRefreshTokenIsNotNull(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.create(user);
    }
}