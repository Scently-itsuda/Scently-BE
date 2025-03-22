package com.itsuda.perfume.dto.response.post;

import java.util.List;

public record CommentsDto(
        List<CommentInfoDto> commentInfos,
        int totalCommentCount
) {
}
