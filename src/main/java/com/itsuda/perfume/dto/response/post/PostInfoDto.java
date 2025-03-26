package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Post;

import java.time.LocalDateTime;

public record PostInfoDto(
        String title,
        String content,
        LocalDateTime createdAt,
        int likeCount,
        Long viewCount,
        int commentCount
) {

    public static PostInfoDto from(Post post) {
        return new PostInfoDto(
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getLikeCount(),
                post.getViewCount(),
                post.getCommentCount()
        );
    }
}
