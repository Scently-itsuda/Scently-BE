package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Comment;

import java.time.LocalDateTime;

public record ChildCommentInfoDto(
        Long commentId,
        Long userId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content,
        int likeCount
) {

    public static ChildCommentInfoDto from(Comment comment) {
        return new ChildCommentInfoDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getImageUrl(),
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getLikeCount()
        );
    }
}
