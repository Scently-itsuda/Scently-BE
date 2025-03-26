package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "childComments")
    List<Comment> findAllByPostAndParentCommentIsNull(Post post);
}
