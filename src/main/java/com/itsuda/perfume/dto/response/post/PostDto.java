package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Post;

import java.time.LocalDateTime;

public record PostDto(
        Long postId,
        String title,
        String content,
        LocalDateTime createdAt,
        Long viewCount,
        int commentCount
) {

    public static PostDto from(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getCommentCount()
        );
    }
}
