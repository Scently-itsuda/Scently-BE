package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.PostLikeNotification;
import com.itsuda.perfume.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeNotificationRepository extends JpaRepository<PostLikeNotification, Long> {

    List<PostLikeNotification> findByLikeReceiver(User likeReceiver);
}
