package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
