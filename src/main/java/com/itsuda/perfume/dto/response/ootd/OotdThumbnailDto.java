package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.OotdImage;

public record OotdThumbnailDto(
        Long ootdId,
        String ootdImageUrl
) {
    public static OotdThumbnailDto from(OotdImage ootdImage) {
        return new OotdThumbnailDto(
                ootdImage.getOotd().getId(),
                ootdImage.getSaveName()
        );
    }
}
