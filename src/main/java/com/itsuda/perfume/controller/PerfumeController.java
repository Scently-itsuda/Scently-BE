package com.itsuda.perfume.controller;

import com.itsuda.perfume.dto.request.PerfumeRequestDto;
import com.itsuda.perfume.dto.response.PerfumeAccordDto;
import com.itsuda.perfume.dto.response.PerfumeDetailDto;
import com.itsuda.perfume.dto.response.PerfumeListDto;
import com.itsuda.perfume.dto.request.ReviewRequestDto;
import com.itsuda.perfume.dto.response.ReviewResponseDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.PerfumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Perfume", description = "향수 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/perfumes")
public class PerfumeController {
    private final PerfumeService perfumeService;

    // ----------------- 향수 관련 API -----------------
    @Operation(summary = "향수 목록 조회", description = "조건에 맞는 향수 목록을 조회합니다.")
    @GetMapping("")
    public ResponseDto<List<PerfumeListDto>> getPerfumes(@ModelAttribute PerfumeRequestDto perfumeRequestDto) {
        return new ResponseDto<>(perfumeService.getPerfumes(perfumeRequestDto));
    }

    @Operation(summary = "향수 어코드 조회", description = "향수 어코드를 조회합니다.")
    @GetMapping("/accords")
    public ResponseDto<List<PerfumeAccordDto>> getAccords() {
        return new ResponseDto<>(perfumeService.getAccords());
    }

    @Operation(summary = "향수 상세 조회", description = "향수 상세 정보를 조회합니다.")
    @GetMapping("/{perfumeId}")
    public ResponseDto<PerfumeDetailDto> getPerfumeDetail(@PathVariable Long perfumeId) {
        return new ResponseDto<>(perfumeService.getPerfumeDetail(perfumeId));
    }

    // ----------------- 리뷰 관련 API -----------------
    @Operation(summary = "리뷰 작성", description = "향수에 대한 리뷰를 작성합니다.")
    @PostMapping("/{perfumeId}/reviews")
    public ResponseDto<ReviewResponseDto> createReview(
            @PathVariable Long perfumeId,
            @RequestBody ReviewRequestDto requestDto) {
        return new ResponseDto<>(perfumeService.createReview(perfumeId, requestDto));
    }

    @Operation(summary = "리뷰 목록 조회", description = "향수의 리뷰 목록을 조회합니다.")
    @GetMapping("/{perfumeId}/reviews")
    public ResponseDto<List<ReviewResponseDto>> getReviews(@PathVariable Long perfumeId) {
        return new ResponseDto<>(perfumeService.getReviews(perfumeId));
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/{perfumeId}/reviews/{reviewId}")
    public ResponseDto<ReviewResponseDto> updateReview(
            @PathVariable Long perfumeId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto requestDto) {
        return new ResponseDto<>(perfumeService.updateReview(perfumeId, reviewId, requestDto));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{perfumeId}/reviews/{reviewId}")
    public ResponseDto<Void> deleteReview(
            @PathVariable Long perfumeId,
            @PathVariable Long reviewId) {
        perfumeService.deleteReview(perfumeId, reviewId);
        return new ResponseDto<>(null);
    }

    @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 추가합니다.")
    @PostMapping("/{perfumeId}/reviews/{reviewId}/like")
    public ResponseDto<ReviewResponseDto> likeReview(
            @PathVariable Long perfumeId,
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        return new ResponseDto<>(perfumeService.likeReview(perfumeId, reviewId, userId));
    }

    @Operation(summary = "리뷰 좋아요 취소", description = "리뷰의 좋아요를 취소합니다.")
    @DeleteMapping("/{perfumeId}/reviews/{reviewId}/like")
    public ResponseDto<ReviewResponseDto> unlikeReview(
            @PathVariable Long perfumeId,
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        return new ResponseDto<>(perfumeService.unlikeReview(perfumeId, reviewId, userId));
    }
}
