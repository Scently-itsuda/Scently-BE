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
import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class OotdRepositoryTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

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

    @Autowired
    private EntityManager em;

    private Perfume perfume;

    private User user;

    @BeforeEach
    void setUp() {
        perfume = createTestPerfume();
        perfumeRepository.save(perfume);
        user = userRepository.save(createTestUser());
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("OOTD 게시물들의 썸네일 정보를 최신순으로 가져온다.")
    @Test
    void getOotdThumbnailsSortedByNewest() {
        // given
        setMockingTime(20);
        Ootd ootd1 = createOotd(1);
        ootdRepository.save(ootd1);
        OotdImage ootd1Image = ootdImageRepository.save(createOotdImage(0, ootd1));

        setMockingTime(30);
        Ootd ootd2 = createOotd(2);
        ootdRepository.save(ootd2);
        OotdImage ootd2Image = ootdImageRepository.save(createOotdImage(0, ootd2));

        setMockingTime(0);
        Ootd ootd3 = createOotd(3);
        ootdRepository.save(ootd3);
        OotdImage ootd3Image = ootdImageRepository.save(createOotdImage(0, ootd3));

        // when
        Pageable pageable = PageRequest.of(0, 3, Sort.by("created_at").descending());
        Page<OotdThumbnailInfo> ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, user.getId());

        // then
        assertThat(ootdThumbnailInfos.getContent()).hasSize(3)
                .extracting(OotdThumbnailInfo::getOotdId, OotdThumbnailInfo::getOotdImageUrl)
                .containsExactly(
                        tuple(ootd2.getId(), ootd2Image.getSaveName()),
                        tuple(ootd1.getId(), ootd1Image.getSaveName()),
                        tuple(ootd3.getId(), ootd3Image.getSaveName())
                );
    }

    @DisplayName("OOTD 게시물 중 사용자가 좋아요를 누른 게시물의 정보도 불러온다.")
    @Test
    void getOotdThumbnailsIncludingUserLiked() {
        // given
        Ootd ootd1 = ootdRepository.save(createOotd(1));
        ootdImageRepository.save(createOotdImage(0, ootd1));

        Ootd ootd2 = ootdRepository.save(createOotd(2));
        ootdImageRepository.save(createOotdImage(0, ootd2));

        Ootd ootd3 = ootdRepository.save(createOotd(3));
        ootdImageRepository.save(createOotdImage(0, ootd3));

        userLikeOotdRepository.save(UserLikeOotd.builder().ootd(ootd1).user(user).build());
        userLikeOotdRepository.save(UserLikeOotd.builder().ootd(ootd3).user(user).build());

        // when
        Pageable pageable = PageRequest.of(0, 3, Sort.by("created_at").descending());
        Page<OotdThumbnailInfo> ootdThumbnailInfos = ootdRepository.findAllIncludingUserLiked(pageable, user.getId());

        // then
        assertThat(ootdThumbnailInfos.getContent()).hasSize(3)
                .extracting(OotdThumbnailInfo::getIsLiked)
                .containsExactly(true, false, true);
    }

    @DisplayName("OOTD 게시글 특정 번호에 해당하는 하나의 게시글과 사진들의 정보를 가져온다.")
    @Test
    void getOotdDetailAndImagesByOotdId() {
        // given
        Ootd ootd = ootdRepository.save(createOotd(1));
        ootdImageRepository.saveAll(List.of(createOotdImage(0, ootd),
                createOotdImage(1, ootd),
                createOotdImage(2, ootd)));

        em.flush();
        em.clear();

        // when
        Ootd findOotd = ootdRepository.findByIdWithOotdImagesAndOotdTags(ootd.getId()).get();
        List<OotdImage> ootdImages = findOotd.getOotdImages();

        // then
        assertThat(findOotd.getId()).isEqualTo(ootd.getId());
        assertThat(ootdImages).containsExactlyElementsOf(ootdImages);
    }

    private void setMockingTime(int minute) {
        given(dateTimeProvider.getNow())
                .willReturn(Optional.of(
                        LocalDateTime.of(2025, 2, 1, 12, minute, 0)
                ));
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