package com.itsuda.perfume.dto.response.notification;

import com.itsuda.perfume.domain.Notification;

public record UserNotification(
        Long notificationId,
        String title,
        String bodyMessage,
        Long targetId,
        String notificationType
) {
    public static UserNotification from(Notification notification) {
        return new UserNotification(notification.getId(), notification.getTitle(), notification.getBodyMessage(),
                notification.getTargetId(), notification.getNotificationType().toString());
    }
}
