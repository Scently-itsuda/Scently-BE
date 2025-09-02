package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdPerfume;

import java.util.List;

public record OotdDetailDto(
        OotdInfoDto ootdInfo,
        UserInfoDto userInfo,
        List<PerfumeInfoDto> perfumeInfoDto
) {

    public static OotdDetailDto from(Ootd ootd, List<OotdPerfume> ootdPerfumes, Boolean isLiked) {
        return new OotdDetailDto(OotdInfoDto.from(ootd, isLiked), UserInfoDto.from(ootd.getUser()),
                ootdPerfumes.stream().map(ootdPerfume -> PerfumeInfoDto.from(ootdPerfume.getPerfume())).toList());
    }
}
