package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.domain.User;

public record UserInfoDto(
        Long userId,
        String profileImageUrl,
        String nickname
) {

    public static UserInfoDto from(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getImageUrl(),
                user.getNickname()
        );
    }
}
