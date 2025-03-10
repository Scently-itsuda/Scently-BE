package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Override
    @EntityGraph(attributePaths = {"ootdImages", "ootdTags"})
    Optional<Ootd> findById(Long id);

    @Query(value = "SELECT o.id AS ootdId, oi.save_name AS ootdImageUrl, " +
            "CASE WHEN ulo.id IS NULL THEN 0 ELSE 1 END AS isLiked FROM ootd o " +
            "LEFT JOIN ootd_image oi ON oi.sequence = 0 AND oi.ootd_id = o.id " +
            "LEFT JOIN user_like_ootd ulo ON ulo.user_id = :userId AND ulo.ootd_id = o.id ",
            nativeQuery = true)
    Page<OotdThumbnailInfo> findByOotdOrderByOotdCreatedAt(Pageable pageable, Long userId);

    interface OotdThumbnailInfo {
        Long getOotdId();
        String getOotdImageUrl();
        int getIsLiked();
    }
}
