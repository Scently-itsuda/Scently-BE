package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Comment;

import java.util.List;

public record CommentsDto(
        List<CommentInfoDto> commentInfos,
        int totalCommentCount
) {

    public static CommentsDto from(List<Comment> comments) {
        return new CommentsDto(
                comments.stream().map(CommentInfoDto::from).toList(),
                comments.stream().mapToInt(comment -> comment.getChildComments().size() + 1).sum()
        );
    }
}
