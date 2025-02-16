package com.itsuda.perfume.service;

import com.itsuda.perfume.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
