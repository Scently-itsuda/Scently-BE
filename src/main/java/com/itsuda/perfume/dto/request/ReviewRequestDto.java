package com.itsuda.perfume.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private Long userId;
    private String content;
    private Float rating;
} 