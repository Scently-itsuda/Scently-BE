package com.itsuda.perfume.dto.request.ootd;

import com.itsuda.perfume.annotation.ValidTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOotdDto(
        @NotBlank(message = "EMPTY_OOTD_CONTENT")
        @Schema(description = "게시글 내용", example = "테스트 내용입니다.", minimum = "1")
        String content,

        @Min(1)
        @Schema(description = "향수 용량", example = "50", minimum = "1")
        int volume,

        @NotNull
        @Size(min = 1, max = 3, message="INVALID_PERFUME_LIST")
        @Schema(description = "향수 아이디", example = "[1, 2, 3...]", minimum = "1", maximum = "3")
        List<Long> perfumeIds,

        @ValidTag
        @Size(max = 10, message = "MAX_TAG_SIZE")
        @Schema(description = "태그", example = "[태그1, 태그2...]", maximum = "10")
        List<String> tagNames
) {
}
