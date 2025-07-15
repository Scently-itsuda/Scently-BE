package com.itsuda.perfume.dto.response.notification;

import com.itsuda.perfume.domain.Notification;

public record UserNotificationDto(
        Long notificationId,
        String title,
        String bodyMessage,
        Long targetId,
        String notificationType
) {
    public static UserNotificationDto from(Notification notification) {
        return new UserNotificationDto(notification.getId(), notification.getTitle(), notification.getBodyMessage(),
                notification.getTargetId(), notification.getNotificationType().toString());
    }
}
