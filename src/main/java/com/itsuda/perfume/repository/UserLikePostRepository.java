package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLikePostRepository extends JpaRepository<UserLikePost, Long> {

    Boolean existsByUserAndPost(User user, Post ootd);

    Optional<UserLikePost> findByPostAndUser(Post post, User user);
}
