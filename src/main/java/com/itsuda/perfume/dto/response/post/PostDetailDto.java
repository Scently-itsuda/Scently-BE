package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.Post;
import com.itsuda.perfume.domain.User;

public record PostDetailDto(
        PostInfoDto postInfo,
        UserInfoDto userInfo
) {

    public static PostDetailDto from(Post post, User user) {
        return new PostDetailDto(PostInfoDto.from(post), UserInfoDto.from(user));
    }
}
