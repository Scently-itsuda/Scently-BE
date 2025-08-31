package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.UserLikeOotd;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserLikeOotdRepositoryTest {

    @Autowired
    private OotdImageRepository ootdImageRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private UserLikeOotdRepository userLikeOotdRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    private Perfume perfume;

    private User user;

    @BeforeEach
    void setUp() {
        perfume = createTestPerfume();
        perfumeRepository.save(perfume);
        user = userRepository.save(createTestUser());
    }

    @DisplayName("사용자가 OOTD 게시글에 좋아요를 눌렀는지 확인한다.")
    @Test
    void userLikeOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));
        ootdImageRepository.save(createOotdImage(0, ootd));
        userLikeOotdRepository.save(UserLikeOotd.builder().user(user).ootd(ootd).build());

        // when
        Boolean isUserLikedOotd = userLikeOotdRepository.existsByUserAndOotd(user, ootd);

        // then
        assertThat(isUserLikedOotd).isTrue();
    }

    @DisplayName("사용자가 OOTD 게시글에 좋아요를 누르지 않았는지 확인한다.")
    @Test
    void userNotLikedOotd() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(0));
        ootdImageRepository.save(createOotdImage(0, ootd));

        // when
        Boolean isUserLikedOotd = userLikeOotdRepository.existsByUserAndOotd(user, ootd);

        // then
        assertThat(isUserLikedOotd).isFalse();
    }

    private static OotdImage createOotdImage(int sequence, Ootd ootd) {
        return OotdImage.builder()
                .originName("test" + sequence)
                .saveName("test" + sequence)
                .sequence(sequence)
                .ootd(ootd)
                .build();
    }

    private Ootd createOotd(int number) {
        return Ootd.builder()
                .volume(10 * number)
                .content("test" + number)
                .user(user)
                .build();
    }

    private static User createTestUser() {
        return User.builder()
                .email("test@test.com")
                .gender(GenderType.MALE)
                .imageUrl("test url")
                .nickname("test nickname")
                .presentation("test")
                .provider(EProvider.GOOGLE)
                .role(ERole.USER)
                .serialId("123")
                .username("test")
                .build();
    }

    private static Perfume createTestPerfume() {
        return Perfume.builder()
                .name("test perfume")
                .imageUri("test url")
                .gender(GenderType.MALE)
                .brand(BrandType.CHANEL)
                .country(CountryType.FRANCE)
                .potential(PotentialType.EDT)
                .description("test desc")
                .registeredAt(LocalDate.of(2025, 2, 1))
                .build();
    }
}