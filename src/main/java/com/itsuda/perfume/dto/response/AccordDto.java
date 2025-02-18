package com.itsuda.perfume.dto.response;

import com.itsuda.perfume.domain.Accord;

public record AccordDto(
        Long id,
        String name
) {
    public static AccordDto from(Accord accord) {
        return new AccordDto(
            accord.getId(),
            accord.getName()
        );
    }
}
