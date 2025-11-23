package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.dto.response.PageInfoDto;
import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public record OotdMainDto(
        List<OotdThumbnailDto> dataList,
        PageInfoDto pageInfo
) {

    public static OotdMainDto from(Page<OotdThumbnailInfo> ootdThumbnails) {
        return new OotdMainDto(ootdThumbnails.stream().map(OotdThumbnailDto::from).toList(), PageInfoDto.from(ootdThumbnails));
    }
}
