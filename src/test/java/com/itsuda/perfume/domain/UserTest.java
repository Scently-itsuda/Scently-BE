package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @DisplayName("리프레시 토큰을 원하는 문자열로 변경할 수 있다.")
    @Test
    void updateUserRefreshToken() {
        // given
        User user = createUser(1);
        String refreshToken = "this is test refresh token";

        // when
        user.updateRefreshToken(refreshToken);

        // then
        assertThat(user.getRefreshToken()).isEqualTo(refreshToken);
    }

    @DisplayName("사용자의 닉네임을 설정하면 기본 사용자 권한이 부여된다.")
    @Test
    void updateUserNickNameAndGivenUserRole() {
        // given
        User user = createUser(1);
        String nickname = "this is test nickname";

        // when
        user.register(nickname);

        // then
        assertThat(user).extracting("nickname", "role")
                .containsExactly(nickname, ERole.USER);
    }

    @DisplayName("사용자의 성별, 생년월일, 닉네임을 설정하면 긱본 사용자 권한이 부여된다.")
    @Test
    void updateUserNickNameAndBirthDateAndGenderTypeAndGivenUserRole() {
        // given
        User user = createUser(1);
        GenderType genderType = GenderType.FEMALE;
        String birthDate = "2000-01-01";
        String nickname = "this is test nickname";

        // when
        user.register(genderType, birthDate, nickname);

        // then
        assertThat(user).extracting("nickname", "role", "gender", "birthDate")
                .containsExactly(nickname, ERole.USER, GenderType.FEMALE, birthDate);
    }

    @DisplayName("사용자의 생일을 기반으로 특정 날짜에서의 나이를 계산한다.")
    @Test
    void getAgeAtSpecificDate() {
        User user = createUser(1);
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

    @DisplayName("사용자의 생년월일을 변경할 수 있다.")
    @Test
    void updateUserBirthDate() {
        // given
        User user = createUser(1);
        String birthDate = "2024-03-01";

        // when
        user.updateBirthDate(birthDate);

        // then
        assertThat(user.getBirthDate()).isEqualTo(birthDate);
    }

    private static User createUser(int number) {
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