package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.User;
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
            "CASE WHEN ulo.id IS NULL THEN FALSE ELSE TRUE END AS isLiked FROM ootd o " +
            "LEFT JOIN ootd_image oi ON oi.sequence = 0 AND oi.ootd_id = o.id " +
            "LEFT JOIN user_like_ootd ulo ON :userId IS NOT NULL AND ulo.user_id = :userId AND ulo.ootd_id = o.id ",
            nativeQuery = true)
    Page<OotdThumbnailInfo> findAllIncludingUserLiked(Pageable pageable, Long userId);

    @Query(value = "SELECT o.id AS ootdId, oi.save_name AS ootdImageUrl FROM ootd o " +
            "INNER JOIN user_like_ootd ulo ON :userId IS NOT NULL AND ulo.user_id = :userId AND ulo.ootd_id = o.id " +
            "LEFT JOIN ootd_image oi ON oi.sequence = 0 AND oi.ootd_id = o.id ",
            nativeQuery = true)
    Page<UserLikeOotdInfo> findAllUserLikeByUser(Pageable pageable, Long userId);

    interface OotdThumbnailInfo {
        Long getOotdId();

        String getOotdImageUrl();

        boolean getIsLiked();
    }

    interface UserLikeOotdInfo {
        Long getOotdId();

        String getOotdImageUrl();
    }
}
