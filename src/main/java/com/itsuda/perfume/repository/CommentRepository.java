package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.childComments JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findByIdWithChildCommentsAndUser(long commentId);

    @EntityGraph(attributePaths = {"childComments", "user"})
    List<Comment> findAllByPostAndParentCommentIsNull(Post post);

    @EntityGraph(attributePaths = {"childComments", "user"})
    List<Comment> findAllByOotdAndParentCommentIsNullAndDeletedAtIsNull(Ootd ootd);
}
