package com.itsuda.perfume.dto.response;

import org.springframework.data.domain.Page;

public record PageInfoDto(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageInfoDto from(Page<T> pageInfo) {
        return new PageInfoDto(
                pageInfo.getNumber(),
                pageInfo.getSize(),
                pageInfo.getTotalElements(),
                pageInfo.getTotalPages()
        );
    }
}
