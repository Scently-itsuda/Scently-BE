package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumesDto;
import com.itsuda.perfume.repository.PerfumeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
}