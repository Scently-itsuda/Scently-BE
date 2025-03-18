package com.itsuda.perfume.dto.response.post;

import java.time.LocalDateTime;
import java.util.List;

public record CommentInfo(
        Long userId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content,
        int likeCount,
        int commentCount,
        List<ChildCommentInfo> childCommentInfos
) {
}
