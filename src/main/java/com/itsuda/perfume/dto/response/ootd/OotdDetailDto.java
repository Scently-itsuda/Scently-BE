package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;

public record OotdDetailDto(
        OotdInfoDto ootdInfo,
        UserInfoDto userInfo,
        PerfumeInfoDto perfumeInfoDto
) {

    public static OotdDetailDto from(Ootd ootd, User user, Perfume perfume) {
        return new OotdDetailDto(OotdInfoDto.from(ootd), UserInfoDto.from(user), PerfumeInfoDto.from(perfume));
    }
}
