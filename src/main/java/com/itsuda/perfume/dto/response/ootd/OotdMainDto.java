package com.itsuda.perfume.dto.response.ootd;

import java.util.List;

public record OotdMainDto(
        List<OotdThumbnailDto> ootdThumbnails
) {
}
