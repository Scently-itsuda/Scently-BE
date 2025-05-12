package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;

import java.util.List;

public record OotdDetailDto(
        OotdInfoDto ootdInfo,
        UserInfoDto userInfo,
        List<PerfumeInfoDto> perfumeInfoDto
) {

    public static OotdDetailDto from(Ootd ootd, User user, List<Perfume> perfumes, Boolean isLiked) {
        return new OotdDetailDto(OotdInfoDto.from(ootd, isLiked), UserInfoDto.from(user),
                perfumes.stream().map(PerfumeInfoDto::from).toList());
    }
}
