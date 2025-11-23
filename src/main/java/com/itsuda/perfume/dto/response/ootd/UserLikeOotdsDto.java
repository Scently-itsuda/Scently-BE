package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.repository.OotdRepository.UserLikeOotdInfo;

import java.util.List;

public record UserLikeOotdsDto(
        List<UserLikeOotdDto> dataList,
        PageInfoDto pageInfo
) {

    public static UserLikeOotdsDto from(List<UserLikeOotdInfo> userLikeOotds, PageInfoDto pageInfo) {
        return new UserLikeOotdsDto(userLikeOotds.stream().map(UserLikeOotdDto::from).toList(), pageInfo);
    }
}
