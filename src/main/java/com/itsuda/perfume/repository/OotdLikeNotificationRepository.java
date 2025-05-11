package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.OotdLikeNotification;
import com.itsuda.perfume.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OotdLikeNotificationRepository extends JpaRepository<OotdLikeNotification, Long> {

    List<OotdLikeNotification> findByLikeReceiver(User likeReceiver);
}
