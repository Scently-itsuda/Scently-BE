package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Comment;

import java.time.LocalDateTime;

public record ChildCommentInfoDto(
        Long userId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content,
        int likeCount
) {

    public static ChildCommentInfoDto from(Comment comment) {
        return new ChildCommentInfoDto(
                comment.getUser().getId(),
                comment.getUser().getImageUrl(),
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getLikeCount()
        );
    }
}
