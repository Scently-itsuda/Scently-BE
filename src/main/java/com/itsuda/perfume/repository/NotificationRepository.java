package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
