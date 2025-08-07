package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.repository.OotdRepository.UserLikeOotdInfo;

public record UserLikeOotdDto(
        Long ootdId,
        String ootdImageUrl
) {
    public static UserLikeOotdDto from(UserLikeOotdInfo userLikeOotdInfo) {
        return new UserLikeOotdDto(userLikeOotdInfo.getOotdId(), userLikeOotdInfo.getOotdImageUrl());
    }
}
