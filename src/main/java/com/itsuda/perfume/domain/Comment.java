package com.itsuda.perfume.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Getter
@SQLDelete(sql = "UPDATE comment SET deleted_at = NOW(), content = '삭제된 댓글입니다', like_count = 0 WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends ModifiableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @ManyToOne
    @JoinColumn(name = "parent_comment")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> childComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Comment(String content, int likeCount, Comment parentComment, Ootd ootd, Post post, User user) {
        this.content = content;
        this.likeCount = likeCount;
        this.parentComment = parentComment;
        this.ootd = ootd;
        this.post = post;
        this.user = user;
    }

    public int increaseLikeCount() {
        return ++this.likeCount;
    }

    public int decreaseLikeCount() {
        return --this.likeCount;
    }
}
