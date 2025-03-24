package com.itsuda.perfume.dto.response.post;

public record PostLikeDto(
        Long postId,
        Boolean isLiked
) {
}
