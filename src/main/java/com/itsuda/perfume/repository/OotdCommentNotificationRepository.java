package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.OotdCommentNotification;
import com.itsuda.perfume.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OotdCommentNotificationRepository extends JpaRepository<OotdCommentNotification, Long> {

    List<OotdCommentNotification> findByCommentReceiver(User user);
}
