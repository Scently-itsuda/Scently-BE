package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdImage;
import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OotdDetailDto(
        Long ootdId,
        LocalDateTime createdAt,
        List<String> ootdIamgeUrls,
        int likeCount,
        int commentCount,
        String gender,
        int age,
        int volume,
        String content,
        List<String> tags,
        Long perfumeId,
        String perfumeBrand,
        String perfumeImageUrl,
        String perfumeName
) {

    public static OotdDetailDto from(Ootd ootd, User user, Perfume perfume) {
        return new OotdDetailDto(ootd.getId(),
                ootd.getCreatedAt(),
                ootd.getOotdImages().stream().map(OotdImage::getSaveName).toList(),
                ootd.getLikeCount(),
                ootd.getCommentCount(),
                user.getGender().toString(),
                user.getAge(LocalDate.now()),
                ootd.getVolume(),
                ootd.getContent(),
                ootd.getOotdTags().stream().map(tag -> tag.getTag().getName()).toList(),
                perfume.getId(),
                perfume.getBrand().toString(),
                perfume.getImageUri(),
                perfume.getName());
    }
}
