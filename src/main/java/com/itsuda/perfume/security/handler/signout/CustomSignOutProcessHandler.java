package com.itsuda.perfume.security.handler.signout;

import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSignOutProcessHandler implements LogoutHandler {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        if (authentication == null) {
            return;
        }

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        userRepository.updateRefreshToken(userPrincipal.getId(), null);
    }
}