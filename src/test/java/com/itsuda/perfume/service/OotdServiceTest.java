package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.domain.type.PotentialType;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.repository.OotdImageRepository;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@Transactional
@SpringBootTest
class OotdServiceTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    OotdService ootdService;

    @Autowired
    OotdImageRepository ootdImageRepository;

    @Autowired
    OotdRepository ootdRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

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

    @DisplayName("OOTD 게시물들의 썸네일의 정보를 최신순으로 조회한다.")
    @Test
    void getOotdThumbnailsSortedByNewest() {
        // given
        Ootd ootd1 = createOotd(1);
        Ootd savedOotd1 = ootdRepository.save(ootd1);
        Ootd ootd2 = createOotd(2);
        Ootd savedOotd2 = ootdRepository.save(ootd2);
        Ootd ootd3 = createOotd(3);
        Ootd savedOotd3 = ootdRepository.save(ootd3);

        setMockingTime(20);
        OotdImage ootdImage1 = ootdImageRepository.save(createOotdImage(0, ootd1));
        setMockingTime(30);
        OotdImage ootdImage2 = ootdImageRepository.save(createOotdImage(0, ootd2));
        setMockingTime(0);
        OotdImage ootdImage3 = ootdImageRepository.save(createOotdImage(0, ootd3));

        // when
        OotdMainDto result = ootdService.getOotdThumbnailsByOrderType(0, 3, OotdOrderType.NEWEST);

        // then
        assertThat(result.dataList()).hasSize(3)
                .extracting("ootdId")
                .containsExactly(savedOotd2.getId(), savedOotd1.getId(), savedOotd3.getId());
    }

    @DisplayName("OOTD 게시글 아이디에 해당하는 OOTD 게시글의 정보와 이미지들을 조회한다.")
    @Test
    void getOotdPostAndImages() {
        // given
        Ootd savedOotd = ootdRepository.save(createOotd(1));
        List<OotdImage> savedOotdImages = ootdImageRepository.saveAll(
                List.of(createOotdImage(0, savedOotd),
                        createOotdImage(1, savedOotd),
                        createOotdImage(2, savedOotd)));
        entityManager.clear();

        // when
        OotdDetailDto ootdDetail = ootdService.getOotdDetailByOotdId(savedOotd.getId());

        // then
        assertThat(ootdDetail).extracting("ootdId", "createdAt")
                .contains(savedOotd.getId(), savedOotd.getCreatedAt());
        assertThat(ootdDetail.ootdInfo().ootdIamgeUrls()).hasSize(3);
    }

    private void setMockingTime(int minute) {
        given(dateTimeProvider.getNow())
                .willReturn(Optional.of(
                        LocalDateTime.of(2025, 2, 1, 12, minute, 0)
                ));
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

    private static OotdImage createOotdImage(int sequence, Ootd ootd) {
        return OotdImage.builder()
                .originName("test" + sequence)
                .saveName("test" + sequence)
                .sequence(sequence)
                .ootd(ootd)
                .build();
    }

    private static User createTestUser() {
        User user = User.builder()
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
        user.updateBirthDate("2000-05-02");
        return user;
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