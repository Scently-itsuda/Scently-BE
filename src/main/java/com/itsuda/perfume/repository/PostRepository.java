package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
