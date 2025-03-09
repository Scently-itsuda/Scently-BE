package com.itsuda.perfume.dto.response.ootd;

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
}
