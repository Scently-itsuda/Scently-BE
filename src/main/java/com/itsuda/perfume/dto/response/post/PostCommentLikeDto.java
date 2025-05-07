package com.itsuda.perfume.dto.response.post;

public record PostCommentLikeDto(
        Boolean isLiked,
        int likeCount
) {
}
