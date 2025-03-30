package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentInfoDto(
        Long commentId,
        Long userId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content,
        int likeCount,
        int commentCount,
        List<ChildCommentInfoDto> childCommentInfos
) {

    public static CommentInfoDto from(Comment comment) {
        return new CommentInfoDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getImageUrl(),
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getChildComments().size(),
                comment.getChildComments().stream().map(ChildCommentInfoDto::from).toList()
        );
    }
}
