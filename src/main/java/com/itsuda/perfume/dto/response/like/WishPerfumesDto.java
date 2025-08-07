package com.itsuda.perfume.dto.response.like;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.dto.response.PageInfoDto;

import java.util.List;

public record WishPerfumesDto(
        List<WishPerfumeDto> dataList,
        PageInfoDto pageInfo
) {
    public static WishPerfumesDto from(List<Perfume> perfumes, PageInfoDto pageInfoDto) {
        return new WishPerfumesDto(perfumes.stream().map(WishPerfumeDto::from).toList(), pageInfoDto);
    }
}
