package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.PerfumeDetail;
import com.itsuda.perfume.domain.PerfumeVolume;
import com.itsuda.perfume.dto.response.PerfumeAccordDto;
import com.itsuda.perfume.dto.response.PerfumeDetailDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.AccordRepository;
import com.itsuda.perfume.repository.PerfumeDetailRepository;
import com.itsuda.perfume.repository.PerfumeReviewRepository;
import com.itsuda.perfume.repository.PerfumeVolumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.itsuda.perfume.dto.request.PerfumeRequestDto;
import com.itsuda.perfume.dto.response.PerfumeListDto;
import com.itsuda.perfume.repository.PerfumeRepository;
import com.itsuda.perfume.repository.PerfumeReviewLikeRepository;
import com.itsuda.perfume.domain.PerfumeAccord;
import com.itsuda.perfume.repository.PerfumeAccordRepository;
import com.itsuda.perfume.domain.Review;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.repository.UserRepository;
import com.itsuda.perfume.dto.request.ReviewRequestDto;
import com.itsuda.perfume.dto.response.ReviewResponseDto;
import com.itsuda.perfume.domain.ReviewLike;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PerfumeService {
    private final PerfumeRepository perfumeRepository;
    private final AccordRepository accordRepository;
    private final PerfumeVolumeRepository perfumeVolumeRepository;
    private final PerfumeDetailRepository perfumeDetailRepository;
    private final PerfumeAccordRepository perfumeAccordRepository;
    private final PerfumeReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PerfumeReviewLikeRepository reviewLikeRepository;

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

    // 향수 리뷰 작성
    @Transactional
    public ReviewResponseDto createReview(Long perfumeId, ReviewRequestDto requestDto) {
        Perfume perfume = perfumeRepository.findById(perfumeId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));
        
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        Review review = Review.builder()
                .content(requestDto.getContent())
                .score(requestDto.getRating())
                .perfume(perfume)
                .user(user)
                .build();

        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    // 향수 리뷰 목록 조회
    public List<ReviewResponseDto> getReviews(Long perfumeId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));

        return reviewRepository.findByPerfume(perfume).stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 향수 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(Long perfumeId, Long reviewId, ReviewRequestDto requestDto) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));
        Review review = reviewRepository.findByIdAndPerfume(reviewId, perfume)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_REVIEW));

        review.update(requestDto.getContent(), requestDto.getRating());
        return ReviewResponseDto.from(review);
    }

    // 향수 리뷰 삭제
    @Transactional
    public void deleteReview(Long perfumeId, Long reviewId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));
        Review review = reviewRepository.findByIdAndPerfume(reviewId, perfume)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_REVIEW));
        reviewRepository.delete(review);
    }

    // 향수 리뷰 좋아요
    @Transactional
    public ReviewResponseDto likeReview(Long perfumeId, Long reviewId, Long userId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));

        Review review = reviewRepository.findByIdAndPerfume(reviewId, perfume)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_REVIEW));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        // 이미 좋아요를 눌렀는지 확인
        if (reviewLikeRepository.existsByReviewAndUser(review, user)) {
            throw new RestApiException(ErrorCode.ALREADY_LIKED_REVIEW);
        }

        // 좋아요 엔티티 생성
        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();
        
        reviewLikeRepository.save(reviewLike);
        review.increaseLikeCount();

        return ReviewResponseDto.from(review);  // 업데이트된 리뷰 정보 반환
    }

    // 향수 리뷰 좋아요 취소
    @Transactional
    public ReviewResponseDto unlikeReview(Long perfumeId, Long reviewId, Long userId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_PERFUME));

        Review review = reviewRepository.findByIdAndPerfume(reviewId, perfume)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_REVIEW));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_USER));

        ReviewLike reviewLike = reviewLikeRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new RestApiException(ErrorCode.NOT_FOUND_REVIEW_LIKE));
        
        reviewLikeRepository.delete(reviewLike);
        review.decreaseLikeCount();

        return ReviewResponseDto.from(review);  // 업데이트된 리뷰 정보 반환
    }
}
