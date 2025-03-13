package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserInfoDto(
        String gender,
        int age
) {

    public static UserInfoDto from(User user) {
        return new UserInfoDto(
                user.getGender().getDescription(),
                user.getAge(LocalDate.now())
        );
    }
}
