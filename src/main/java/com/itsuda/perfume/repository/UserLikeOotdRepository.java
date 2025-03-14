package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikeOotd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLikeOotdRepository extends JpaRepository<UserLikeOotd, Long> {

    Boolean existsByUserAndOotd(User user, Ootd ootd);

    Optional<UserLikeOotd> findByOotdAndUser(Ootd ootd, User user);
}
