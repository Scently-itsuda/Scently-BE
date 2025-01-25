package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.dto.request.UserResisterDto;
import com.itsuda.perfume.dto.response.JwtTokenDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public JwtTokenDto registerUserInfo(Long userId, UserResisterDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        user.register(
            requestDto.gender(), 
            requestDto.birthDate(), 
            requestDto.nickname()
        );
        
        return jwtUtil.generateTokens(userId, ERole.USER);
    }

    @Transactional
    public JwtTokenDto reissue(final String refreshToken) {
        return jwtUtil.reissue(refreshToken);
    }
}
