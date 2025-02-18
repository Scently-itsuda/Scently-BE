package com.itsuda.perfume.dto.response;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.PerfumeDetail;
import com.itsuda.perfume.domain.PerfumeVolume;
import com.itsuda.perfume.domain.PerfumeAccord;
import java.util.List;

public record PerfumeDetailDto(
        Long perfumeId,
        String imageURL,
        String brand,
        String name,
        List<PerfumeVolumeDto> perfumeVolumes,
        String potential,
        NoteAccordsDto accords,
        String description, // 향 설명
        String detail // 상품 고시 정보
) {
    public static PerfumeDetailDto from(Perfume perfume, List<PerfumeVolume> perfumeVolumes, 
            List<PerfumeAccord> perfumeAccords, PerfumeDetail perfumeDetail) {
        return new PerfumeDetailDto(
                perfume.getId(),
                perfume.getImageUri(),
                perfume.getBrand().getDescription(),
                perfume.getName(),
                PerfumeVolumeDto.from(perfumeVolumes),
                perfume.getPotential().getDescription(),
                NoteAccordsDto.from(perfumeAccords),
                perfume.getDescription(),
                perfumeDetail.getContent()
        );
    }
}