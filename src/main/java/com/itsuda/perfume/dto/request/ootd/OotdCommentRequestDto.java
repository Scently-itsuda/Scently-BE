package com.itsuda.perfume.dto.request.ootd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record OotdCommentRequestDto(
        Long commentId,

        @NotBlank(message = "INVALID_COMMENT_COMMENT")
        @Schema(description = "댓글 내용", example = "테스트 댓글입니다.", minimum = "1")
        String comment
) {
}
