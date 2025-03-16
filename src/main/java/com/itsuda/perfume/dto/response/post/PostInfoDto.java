package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Post;

import java.time.LocalDateTime;

public record PostInfoDto(
        Long postId,
        String title,
        String content,
        LocalDateTime createdAt,
        Long viewCount,
        int commentCount
) {

    public static PostInfoDto from(Post post) {
        return new PostInfoDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getCommentCount()
        );
    }
}
