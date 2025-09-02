package com.itsuda.perfume.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @DisplayName("자유게시글의 좋아요 수를 1만큼 증가시킨다.")
    @Test
    void increaseLikeCount() {
        // given
        Post post = createPost(1);
        int beforeLikeCount = post.getLikeCount();

        // when
        post.increaseLikeCount();

        // then
        assertThat(beforeLikeCount + 1).isEqualTo(post.getLikeCount());
    }

    @DisplayName("자유게시글의 좋아요 수를 1만큼 감소시킨다.")
    @Test
    void decreaseLikeCount() {
        // given
        Post post = createPost(1);
        int beforeLikeCount = post.getLikeCount();

        // when
        post.decreaseLikeCount();

        // then
        assertThat(beforeLikeCount - 1).isEqualTo(post.getLikeCount());
    }

    private Post createPost(int num) {
        return Post.builder()
                .title("test" + num)
                .content("test content" + num)
                .user(null).build();
    }
}