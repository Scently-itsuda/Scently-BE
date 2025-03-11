package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;

import java.time.LocalDateTime;
import java.util.List;

public record OotdInfoDto(
        Long ootdId,
        LocalDateTime createdAt,
        List<String> ootdIamgeUrls,
        int likeCount,
        int commentCount,
        int volume,
        String content,
        List<String> tags,
        Boolean isLiked
) {

    public static OotdInfoDto from(Ootd ootd, Boolean isLiked) {
        return new OotdInfoDto(
                ootd.getId(),
                ootd.getCreatedAt(),
                ootd.getOotdImages().stream().map(OotdImage::getSaveName).toList(),
                ootd.getLikeCount(),
                ootd.getCommentCount(),
                ootd.getVolume(),
                ootd.getContent(),
                ootd.getOotdTags().stream().map(ootdTag -> ootdTag.getTag().getName()).toList(),
                isLiked
        );
    }
}
