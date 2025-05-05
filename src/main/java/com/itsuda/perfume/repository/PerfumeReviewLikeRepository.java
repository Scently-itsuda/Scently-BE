package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Review;
import com.itsuda.perfume.domain.ReviewLike;
import com.itsuda.perfume.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfumeReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByReviewAndUser(Review review, User user);
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
}