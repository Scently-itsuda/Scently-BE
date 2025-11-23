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
import static org.mockito.BDDMockito.given;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
class OotdImageRepositoryTest {

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @Autowired
    private OotdImageRepository ootdImageRepository;

    @Autowired
    private OotdRepository ootdRepository;

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
        MockitoAnnotations.openMocks(this);
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @DisplayName("OOTD 게시물들의 썸네일 정보를 최신순으로 가져온다.")
    @Test
    void getOotdThumbnailsSortedByNewest() {
        // given
        Ootd ootd1 = createOotd(1);
        Ootd ootd2 = createOotd(2);
        Ootd ootd3 = createOotd(3);
        ootdRepository.saveAll(List.of(ootd1, ootd2, ootd3));

        setMockingTime(20);
        OotdImage ootd1Image = createOotdImage(0, ootd1);
        ootdImageRepository.save(ootd1Image);
        setMockingTime(10);
        OotdImage ootd2Image = createOotdImage(0, ootd2);
        ootdImageRepository.save(ootd2Image);
        setMockingTime(0);
        OotdImage ootd3Image = createOotdImage(0, ootd3);
        ootdImageRepository.save(ootd3Image);

        // when
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Page<OotdImage> ootdImages = ootdImageRepository.findByOotdOrderByOotdCreatedAt(pageable);

        // then
        assertThat(ootdImages.getContent()).hasSize(3)
                .containsExactly(ootd1Image, ootd2Image, ootd3Image);
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