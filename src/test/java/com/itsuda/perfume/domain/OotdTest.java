package com.itsuda.perfume.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OotdTest {

    @DisplayName("OOTD 게시글의 좋아요 수를 1만큼 증가시킨다.")
    @Test
    void increaseLikeCount() {
        // given
        Ootd ootd = Ootd.builder().build();
        int beforeLikeCount = ootd.getLikeCount();

        // when
        ootd.increaseLikeCount();

        // then
        assertThat(beforeLikeCount + 1).isEqualTo(ootd.getLikeCount());
    }

    @DisplayName("OOTD 게시글의 좋아요 수를 1만큼 감소시킨다.")
    @Test
    void decreaseLikeCount() {
        // given
        Ootd ootd = Ootd.builder().build();
        int beforeLikeCount = ootd.getLikeCount();

        // when
        ootd.decreaseLikeCount();

        // then
        assertThat(beforeLikeCount - 1).isEqualTo(ootd.getLikeCount());
    }
}