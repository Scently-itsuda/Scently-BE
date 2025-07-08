package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.WishPerfume;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PerfumeOrderType;
import com.itsuda.perfume.domain.type.PotentialType;
import com.itsuda.perfume.dto.request.like.WishPerfumeRequestDto;
import com.itsuda.perfume.dto.response.like.WishPerfumeDto;
import com.itsuda.perfume.dto.response.like.WishPerfumesDto;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumesDto;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.repository.WishPerfumeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PerfumeServiceTest {

    @Autowired
    private PerfumeService perfumeService;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishPerfumeRepository wishPerfumeRepository;

    @Autowired
    EntityManager em;

    @DisplayName("모든 향수들의 목록을 조회할 수 있다.")
    @Test
    void getAllPerfumes() {
        // given
        List<Perfume> perfumes = perfumeRepository.saveAll(List.of(createPerfume("test1"),
                createPerfume("test2"),
                createPerfume("test3")));
        em.flush();
        em.clear();

        // when
        OotdPerfumesDto result = perfumeService.getAllPerfumes();

        // then
        assertThat(result.perfumes())
                .extracting("perfumeId")
                .containsSequence(perfumes.stream().map(Perfume::getId).toList());
    }

    @DisplayName("사용자는 향수를 위시에 담고, 본인이 담은 위시 항목에서 해당 향수를 볼 수 있다.")
    @Test
    void userCanSendWishToPerfume() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        User user = userRepository.save(createTestUser());
        em.flush();
        em.clear();

        // when
        perfumeService.sendWishToPerfume(perfume.getId(), user.getId());

        // then
        assertThat(wishPerfumeRepository.findByPerfumeAndCustomer(perfume, user)).isPresent();
    }

    @DisplayName("향수에 위시를 요청하면 향수의 위시 수가 1만큼 오르고 사용자는 향수에 위시를 누른 것을 확인할 수 있다.")
    @Test
    void increaseOotdCommentLikesAndCheckLike() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        User user = userRepository.save(createTestUser());
        int originWishCount = perfume.getWishCount();
        em.flush();
        em.clear();

        // when
        perfumeService.sendWishToPerfume(perfume.getId(), user.getId());
        Optional<WishPerfume> wishPerfume = wishPerfumeRepository.findByPerfumeAndCustomer(perfume, user);

        // then
        assertThat(wishPerfume).isPresent();
        assertThat(wishPerfume.get().getPerfume().getWishCount()).isEqualTo(originWishCount + 1);
    }

    @DisplayName("사용자가 위시를 누른 향수에 위시를 한번 더 누르면 위시가 취소된다.")
    @Test
    void cancelLikeToLikedOotdComment() {
        // given
        Perfume perfume = perfumeRepository.save(createPerfume("test"));
        User user = userRepository.save(createTestUser());
        perfumeService.sendWishToPerfume(perfume.getId(), user.getId());
        int originWishCount = perfume.getWishCount();
        em.flush();
        em.clear();

        // when
        perfumeService.sendWishToPerfume(perfume.getId(), user.getId());
        Optional<WishPerfume> wishPerfume = wishPerfumeRepository.findByPerfumeAndCustomer(perfume, user);

        // then
        assertThat(wishPerfume).isPresent();
        assertThat(wishPerfume.get().getPerfume().getWishCount()).isEqualTo(originWishCount - 1);
    }

    @DisplayName("사용자가 좋아요를 누른 향수 목록을 확인할 수 있다.")
    @Test
    void getAllWishPerfumes() {
        // given
        Perfume perfume1 = perfumeRepository.save(createPerfume("test1"));
        Perfume perfume2 = perfumeRepository.save(createPerfume("test2"));
        Perfume perfume3 = perfumeRepository.save(createPerfume("test3"));
        User user = userRepository.save(createTestUser());
        perfumeService.sendWishToPerfume(perfume1.getId(), user.getId());
        perfumeService.sendWishToPerfume(perfume3.getId(), user.getId());
        WishPerfumeRequestDto wishPerfumeRequestDto = new WishPerfumeRequestDto(null, null, null, null, null, null, null);

        // when
        WishPerfumesDto wishPerfumes = perfumeService.getAllWishPerfumes(
                wishPerfumeRequestDto, 0, 3, PerfumeOrderType.REGISTERED_AT_DESCENDING, user.getId());

        // then
        assertThat(wishPerfumes.dataList()).size().isEqualTo(2);
        assertThat(wishPerfumes.dataList())
                .extracting("name")
                .contains("test1 perfume", "test3 perfume");
    }

    private static Perfume createPerfume(String name) {
        return Perfume.builder()
                .name(name + " perfume")
                .imageUri(name + " url")
                .gender(GenderType.MALE)
                .brand(BrandType.CHANEL)
                .country(CountryType.FRANCE)
                .potential(PotentialType.EDT)
                .description(name + " desc")
                .registeredAt(LocalDate.of(2025, 2, 1))
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
}