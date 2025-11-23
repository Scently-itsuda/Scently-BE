package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends ModifiableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String bodyMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_sender_id")
    private User notificationSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_receiver_id")
    private User notificationReceiver;

    private Long targetId;

    private NotificationType notificationType;

    @Builder
    private Notification(String title, String bodyMessage, User notificationSender, User notificationReceiver, Long targetId, NotificationType notificationType) {
        this.title = title;
        this.bodyMessage = bodyMessage;
        this.notificationSender = notificationSender;
        this.notificationReceiver = notificationReceiver;
        this.targetId = targetId;
        this.notificationType = notificationType;
    }
}
