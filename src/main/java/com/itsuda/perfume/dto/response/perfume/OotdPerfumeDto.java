package com.itsuda.perfume.dto.response.perfume;

public record OotdPerfumeDto(
        Long perfumeId,
        String imageUri,
        String brand,
        String name
) {
}
