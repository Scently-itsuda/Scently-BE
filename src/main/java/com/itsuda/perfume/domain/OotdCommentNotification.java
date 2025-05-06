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
public class OotdCommentNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String bodyMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_writer_id")
    private User commentWriter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_receiver_id")
    private User commentReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @Builder
    private OotdCommentNotification(String title, String bodyMessage, User commentWriter, User commentReceiver, Comment comment, Ootd ootd) {
        this.title = title;
        this.bodyMessage = bodyMessage;
        this.commentWriter = commentWriter;
        this.commentReceiver = commentReceiver;
        this.comment = comment;
        this.ootd = ootd;
    }
}
