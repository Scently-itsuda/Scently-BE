package com.itsuda.perfume.dto.request.post;

import com.itsuda.perfume.annotation.ValidTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostDto(
        @NotBlank(message = "EMPTY_POST_TITLE")
        @Schema(description = "게시글 제목", example = "테스트 제목입니다.", minimum = "1")
        String title,

        @NotBlank(message = "EMPTY_POST_CONTENT")
        @Schema(description = "게시글 내용", example = "테스트 내용입니다.", minimum = "1")
        String content,

        @ValidTag
        @Size(max = 10, message = "MAX_TAG_SIZE")
        @Schema(description = "태그", example = "[태그1, 태그2...]", maximum = "10")
        List<String> tagNames
) {
}
