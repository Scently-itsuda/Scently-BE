package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("사용자의 생일을 기반으로 특정 날짜에서의 나이를 계산한다.")
    @Test
    void getAge() {
        User user = createTestUser(1);
        user.updateBirthDate("2024-03-01");

        List<Integer> ages = List.of(
                user.getAge(LocalDate.of(2000, 3, 1)),
                user.getAge(LocalDate.of(2024, 3, 1)),
                user.getAge(LocalDate.of(2023, 3, 1)),
                user.getAge(LocalDate.of(2023, 2, 28)),
                user.getAge(LocalDate.of(2023, 3, 2))
        );

        assertThat(ages).containsExactly(24, 0, 1, 1, 0);
    }

    private static User createTestUser(int number) {
        return User.builder()
                .email(number + "test@test.com")
                .gender(GenderType.MALE)
                .imageUrl("test url" + number)
                .nickname("test nickname" + number)
                .presentation("test" + number)
                .provider(EProvider.GOOGLE)
                .role(ERole.USER)
                .serialId("123" + number)
                .username("test" + number)
                .build();
    }
}