package com.itsuda.perfume.domain;

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
public class OotdLikeNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String bodyMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_sender_id")
    private User likeSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "like_receiver_id")
    private User likeReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @Builder
    private OotdLikeNotification(String title, String bodyMessage, User likeSender, User likeReceiver, Ootd ootd) {
        this.title = title;
        this.bodyMessage = bodyMessage;
        this.likeSender = likeSender;
        this.likeReceiver = likeReceiver;
        this.ootd = ootd;
    }
}
