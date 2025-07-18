package com.itsuda.perfume.dto.response.like;

import com.itsuda.perfume.domain.Perfume;

public record WishPerfumeDto(
        Long perfumeId,
        String imageUri,
        String brand,
        String name
) {

    public static WishPerfumeDto from(Perfume perfume) {
        return new WishPerfumeDto(perfume.getId(), perfume.getImageUri(), perfume.getBrand().getDescription(),
                perfume.getName());
    }
}
