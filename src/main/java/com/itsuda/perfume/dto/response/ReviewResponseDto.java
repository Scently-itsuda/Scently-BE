package com.itsuda.perfume.dto.response;

import com.itsuda.perfume.domain.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {
    private Long id;
    private String content;
    private Float score;
    private Integer likeCount;
    private Long userId;
    private String userName;
    private Long perfumeId;
    private String perfumeName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .score(review.getScore())
                .likeCount(review.getLikeCount())
                .userId(review.getUser().getId())
                .userName(review.getUser().getNickname())
                .perfumeId(review.getPerfume().getId())
                .perfumeName(review.getPerfume().getName())
                .createdAt(review.getCreatedAt())
                .build();
    }
} 