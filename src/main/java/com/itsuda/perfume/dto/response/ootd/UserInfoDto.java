package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.User;

import java.time.LocalDate;

public record UserInfoDto(
        Long userId,
        String profileImageUrl,
        String nickname,
        String gender,
        int age
) {

    public static UserInfoDto from(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getImageUrl(),
                user.getNickname(),
                user.getGender().getDescription(),
                user.getAge(LocalDate.now())
        );
    }
}
