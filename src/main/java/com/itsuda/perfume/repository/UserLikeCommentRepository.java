package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Comment;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLikeCommentRepository extends JpaRepository<UserLikeComment, Long> {

    Optional<UserLikeComment> findByCommentAndUser(Comment comment, User user);

    Boolean existsByUserAndComment(User user, Comment comment);
}
