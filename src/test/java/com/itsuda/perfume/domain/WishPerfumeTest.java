package com.itsuda.perfume.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WishPerfumeTest {


    @DisplayName("향수의 위시 상태를 반대로 변경할 수 있다(위시o -> 위시x).")
    @Test
    void test() {
        // given
        WishPerfume wishPerfume = createWishPerfume();
        boolean wished = wishPerfume.isWished();

        // when
        wishPerfume.changeWishStatus();

        // then
        Assertions.assertThat(wishPerfume.isWished()).isEqualTo(!wished);
    }

    public WishPerfume createWishPerfume() {
        return WishPerfume.builder()
                .perfume(null)
                .customer(null).build();
    }
}