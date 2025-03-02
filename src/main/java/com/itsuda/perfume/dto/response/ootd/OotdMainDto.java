package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.dto.response.PageInfoDto;

import java.util.List;

public record OotdMainDto(
        List<OotdThumbnailDto> dataList,
        PageInfoDto pageInfo
) {
}
