package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Notification;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.notification.UserNotification;
import com.itsuda.perfume.dto.response.notification.UserNotifications;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.NotificationRepository;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itsuda.perfume.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public UserNotifications getAllNotificationsByUserId(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_USER));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> userNotifications = notificationRepository.findByNotificationReceiver(user, pageable);

        return new UserNotifications(userNotifications.getContent().stream().map(UserNotification::from).toList(), PageInfoDto.from(userNotifications));
    }
}
