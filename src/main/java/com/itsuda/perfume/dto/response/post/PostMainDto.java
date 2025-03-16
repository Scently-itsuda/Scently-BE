package com.itsuda.perfume.dto.response.post;

import com.itsuda.perfume.dto.response.PageInfoDto;

import java.util.List;

public record PostMainDto(
        List<PostInfoDto> dataList,
        PageInfoDto pageInfoDto
) {
}
