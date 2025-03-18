package com.itsuda.perfume.dto.response.post;

public record PostDetailDto(
        PostInfoDto postInfo,
        UserInfoDto userInfo
) {
}
