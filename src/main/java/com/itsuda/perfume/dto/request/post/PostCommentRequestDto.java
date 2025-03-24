package com.itsuda.perfume.dto.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PostCommentRequestDto(
        Long commentId,

        @NotBlank
        @Schema(description = "댓글 내용", example = "텟트 댓글입니다.", minimum = "1")
        String comment
) {
}
