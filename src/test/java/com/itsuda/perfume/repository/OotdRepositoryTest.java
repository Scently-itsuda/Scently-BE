package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
class OotdRepositoryTest {

    @Autowired
    private OotdImageRepository ootdImageRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Perfume perfume;

    private User user;

    private Ootd ootd;

    private List<OotdImage> ootdImages;

    @BeforeEach
    void setUp() {
        perfume = createTestPerfume();
        perfumeRepository.save(perfume);
        user = userRepository.save(createTestUser());
        ootd = ootdRepository.save(createOotd(1));
        List<OotdImage> ootdImages = ootdImageRepository.saveAll(
                List.of(createOotdImage(0, ootd),
                        createOotdImage(1, ootd),
                        createOotdImage(2, ootd)));
        entityManager.clear();
    }

    @DisplayName("OOTD 게시글 특정 번호에 해당하는 하나의 게시글과 사진들의 정보를 가져온다.")
    @Test
    void getOotdDetailAndImagesByOotdId() {
        // when
        Ootd findOotd = ootdRepository.findById(ootd.getId()).get();
        List<OotdImage> ootdImages = findOotd.getOotdImages();

        // then
        assertThat(findOotd.getId()).isEqualTo(ootd.getId());
        assertThat(ootdImages).containsExactlyElementsOf(ootdImages);
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
                .likeCount(number)
                .commentCount(number)
                .volume(10 * number)
                .content("test" + number)
                .perfume(perfume)
                .user(user)
                .build();
    }

    private static User createTestUser() {
        return User.builder()
                .email("test@test.com")
                .gender(GenderType.MALE)
                .id(0L)
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