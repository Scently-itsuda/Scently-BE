package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    Optional<UserFcmToken> findByUser(User user);
}
