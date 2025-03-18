package com.itsuda.perfume.dto.response.post;

import java.time.LocalDateTime;

public record ChildCommentInfo(
        Long userId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content,
        int likeCount,
        int commentCount
) {
}
