package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikeOotd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeOotdRepository extends JpaRepository<UserLikeOotd, Long> {

    Boolean existsByUserAndOotd(User user, Ootd ootd);
}
