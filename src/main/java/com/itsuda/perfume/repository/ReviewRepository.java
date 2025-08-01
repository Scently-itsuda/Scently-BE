package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
