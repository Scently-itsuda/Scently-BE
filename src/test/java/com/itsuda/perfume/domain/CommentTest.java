package com.itsuda.perfume.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommentTest {

    @DisplayName("현재 댓글의 수를 1개 늘릴 수 있다.")
    @Test
    void increaseCommentLikeCount() {
        // given
        Comment comment = createComment(1);
        int beforeLikeCount = comment.getLikeCount();

        // when
        comment.increaseLikeCount();

        // then
        assertThat(beforeLikeCount + 1).isEqualTo(comment.getLikeCount());
    }

    @DisplayName("현재 댓글의 수를 1개 줄일 수 있다.")
    @Test
    void decreaseCommentLikeCount() {
        // given
        Comment comment = createComment(1);
        int beforeLikeCount = comment.getLikeCount();

        // when
        comment.decreaseLikeCount();

        // then
        assertThat(beforeLikeCount - 1).isEqualTo(comment.getLikeCount());
    }

    private Comment createComment(int num) {
        return Comment.builder()
                .content("test content" + num)
                .parentComment(null)
                .ootd(null)
                .post(null)
                .user(null).build();
    }
}