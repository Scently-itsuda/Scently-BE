package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PerfumeTest {

    @DisplayName("현재 향수를 담은 위시 카운트 수를 1개 늘릴 수 있다.")
    @Test
    void increasePerfumeWishCount() {
        // given
        Perfume perfume = createPerfume(1);
        int beforeWishCount = perfume.getWishCount();

        // when
        perfume.increaseWishCount();

        // then
        assertThat(beforeWishCount + 1).isEqualTo(perfume.getWishCount());
    }

    @DisplayName("현재 향수를 담은 위시 카운트 수를 1개 줄일 수 있다.")
    @Test
    void decreasePerfumeWishCount() {
        // given
        Perfume perfume = createPerfume(1);
        int beforeWishCount = perfume.getWishCount();

        // when
        perfume.decreaseWishCount();

        // then
        assertThat(beforeWishCount - 1).isEqualTo(perfume.getWishCount());
    }

    private Perfume createPerfume(int num) {
        return Perfume.builder()
                .name("test" + num)
                .imageUri("test uri" + num)
                .gender(GenderType.MALE)
                .brand(BrandType.CHANEL)
                .country(CountryType.USA)
                .potential(PotentialType.EDT)
                .description("test" + num)
                .registeredAt(LocalDate.now()).build();
    }
}