package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.PostCommentNotification;
import com.itsuda.perfume.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentNotificationRepository extends JpaRepository<PostCommentNotification, Long> {

    List<PostCommentNotification> findByCommentReceiver(User user);
}
