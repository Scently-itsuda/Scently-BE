package com.itsuda.perfume.dto.response.ootd;

import com.itsuda.perfume.domain.Perfume;

import java.time.LocalDateTime;
import java.util.List;

public record PerfumeInfoDto(
        Long perfumeId,
        String perfumeBrand,
        String perfumeImageUrl,
        String perfumeName
) {

    public static PerfumeInfoDto from(Perfume perfume) {
        return new PerfumeInfoDto(
                perfume.getId(),
                perfume.getBrand().getDescription(),
                perfume.getImageUri(),
                perfume.getName()
        );
    }
}
