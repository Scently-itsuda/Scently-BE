package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByNotificationReceiver(User receiver);

    Page<Notification> findByNotificationReceiver(User receiver, Pageable pageable);
}
