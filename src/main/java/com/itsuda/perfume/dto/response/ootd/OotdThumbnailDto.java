package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.repository.OotdRepository.OotdThumbnailInfo;

public record OotdThumbnailDto(
        Long ootdId,
        String ootdImageUrl,
        Boolean isLiked
) {
    public static OotdThumbnailDto from(OotdThumbnailInfo ootdThumbnailInfo) {
        return new OotdThumbnailDto(
                ootdThumbnailInfo.getOotdId(),
                ootdThumbnailInfo.getOotdImageUrl(),
                ootdThumbnailInfo.getIsLiked() == 1
        );
    }
}
