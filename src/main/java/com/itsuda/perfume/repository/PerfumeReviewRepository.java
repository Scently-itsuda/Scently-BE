package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfumeReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByPerfume(Perfume perfume);
}
