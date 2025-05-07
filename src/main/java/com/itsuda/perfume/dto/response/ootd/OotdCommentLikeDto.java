package com.itsuda.perfume.dto.response.ootd;

public record OotdCommentLikeDto(
        Boolean isLiked,
        int likeCount
) {
}
