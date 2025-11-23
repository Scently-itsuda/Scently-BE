package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findById(Long id);

    Page<Post> findAllByDeletedAtIsNullAndTitleContains(Pageable pageable, String keyword);
}
