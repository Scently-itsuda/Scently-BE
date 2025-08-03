package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.PerfumeAccord;
import com.itsuda.perfume.domain.PerfumeDetail;
import com.itsuda.perfume.domain.PerfumeVolume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.WishPerfume;
import com.itsuda.perfume.domain.type.PerfumeOrderType;
import com.itsuda.perfume.dto.request.PerfumeRequestDto;
import com.itsuda.perfume.dto.request.like.WishPerfumeRequestDto;
import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.dto.response.PerfumeAccordDto;
import com.itsuda.perfume.dto.response.PerfumeDetailDto;
import com.itsuda.perfume.dto.response.PerfumeListDto;
import com.itsuda.perfume.dto.response.like.WishPerfumesDto;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumeDto;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumesDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.AccordRepository;
import com.itsuda.perfume.repository.PerfumeAccordRepository;
import com.itsuda.perfume.repository.PerfumeDetailRepository;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.PerfumeVolumeRepository;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.repository.WishPerfumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PerfumeService {
    private final PerfumeRepository perfumeRepository;
    private final AccordRepository accordRepository;
    private final PerfumeVolumeRepository perfumeVolumeRepository;
    private final PerfumeDetailRepository perfumeDetailRepository;
    private final PerfumeAccordRepository perfumeAccordRepository;
    private final UserRepository userRepository;
    private final WishPerfumeRepository wishPerfumeRepository;

    // 향수 목록 조회
    public List<PerfumeListDto> getPerfumes(PerfumeRequestDto perfumeRequestDto) {
        return perfumeRepository.findBySearchOptions(
                perfumeRequestDto.getMinPrice(),
                perfumeRequestDto.getMaxPrice(),
                perfumeRequestDto.getGenders(),
                perfumeRequestDto.getAccords(),
                perfumeRequestDto.getPotentials(),
                perfumeRequestDto.getBrands(),
                perfumeRequestDto.getCountries()
        ).stream().map(PerfumeListDto::from).toList();
    }

    // 향수 어코드 조회
    public List<PerfumeAccordDto> getAccords() {
        return accordRepository.findAll().stream().map(PerfumeAccordDto::from).toList();
    }

    // 향수 상세 조회
    public PerfumeDetailDto getPerfumeDetail(Long perfumeId) {
        Perfume perfume = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));

        List<PerfumeVolume> perfumeVolume = perfumeVolumeRepository.findByPerfume(perfume);
        if (perfumeVolume.isEmpty()) {
            throw new RestApiException(ErrorCode.NOT_FOUND_PERFUME_VOLUME);
        }

        // PerfumeAccord 정보를 직접 조회
        List<PerfumeAccord> perfumeAccords = perfumeAccordRepository.findByPerfume(perfume);
        if (perfumeAccords.isEmpty()) {
            throw new RestApiException(ErrorCode.NOT_FOUND_ACCORD);
        }

        PerfumeDetail perfumeDetail = perfumeDetailRepository.findByPerfume(perfume)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME_DETAIL));

        return PerfumeDetailDto.from(perfume, perfumeVolume, perfumeAccords, perfumeDetail);
    }

    public OotdPerfumesDto getAllPerfumes() {
        List<OotdPerfumeDto> ootdPerfumes = perfumeRepository.findAll().stream()
                .map(perfume -> new OotdPerfumeDto(perfume.getId(), perfume.getImageUri(),
                        perfume.getBrand().getDescription(), perfume.getName())).toList();

        return new OotdPerfumesDto(ootdPerfumes);
    }

    public void sendWishToPerfume(Long perfumeId, Long userId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));
        Optional<WishPerfume> wishPerfume = wishPerfumeRepository.findByPerfumeAndCustomer(perfume, user);

        wishPerfume.ifPresentOrElse(
                wish -> {
                    if (wish.changeWishStatus()) {
                        perfume.increaseLikeCount();
                    } else {
                        perfume.decreaseLikeCount();
                    }
                },
                () -> {
                    wishPerfumeRepository.save(WishPerfume.builder().perfume(perfume).customer(user).build());
                    perfume.increaseLikeCount();
                }
        );
    }

    public WishPerfumesDto getAllWishPerfumes(WishPerfumeRequestDto wishPerfumeRequestDto, int page, int size, PerfumeOrderType perfumeOrderType, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));
        Pageable pageable = switch (perfumeOrderType) {
            case REGISTERED_AT_DESCENDING -> PageRequest.of(page, size, Sort.by("registeredAt").descending());
            case REGISTERED_AT_ASCENDING -> PageRequest.of(page, size, Sort.by("registeredAt").ascending());
            case POPULAR_DESCENDING -> PageRequest.of(page, size, Sort.by("wishCount").descending());
            case POPULAR_ASCENDING -> PageRequest.of(page, size, Sort.by("wishCount").ascending());
            default -> PageRequest.of(page, size, Sort.by("registeredAt").descending());
        };

        Page<Perfume> wishPerfumes = perfumeRepository.findAllWishPerfumeBySearchOptions(
                pageable,
                wishPerfumeRequestDto.getMinPrice(),
                wishPerfumeRequestDto.getMaxPrice(),
                wishPerfumeRequestDto.getGenders(),
                wishPerfumeRequestDto.getAccords(),
                wishPerfumeRequestDto.getPotentials(),
                wishPerfumeRequestDto.getBrands(),
                wishPerfumeRequestDto.getCountries(),
                user);
        return WishPerfumesDto.from(wishPerfumes.getContent(), PageInfoDto.from(wishPerfumes));
    }
}
