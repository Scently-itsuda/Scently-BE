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

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info(username);
        UserRepository.UserSecurityForm user = userRepository.findUserIdAndRoleBySerialId(Long.valueOf(username))
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.create(user);
    }

    public UserDetails loadUserById(Long userId) throws RestApiException {
        UserRepository.UserSecurityForm user = userRepository.findByIdAndRefreshTokenIsNotNull(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        return CustomUserDetails.create(user);
    }
}