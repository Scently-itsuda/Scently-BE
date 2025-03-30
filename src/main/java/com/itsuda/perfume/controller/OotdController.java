package com.itsuda.perfume.controller;

import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.request.ootd.OotdCommentRequestDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdLikeDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.OotdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ootds")
@Tag(name = "Ootd", description = "OOTD 게시글 관련 API")
public class OotdController {

    private final OotdService ootdService;

    @Operation(summary = "OOTD 게시글 목록 조회", description = "OOTD 게시글들을 정렬 순서에 맞게 조회합니다.")
    @GetMapping
    public ResponseDto<OotdMainDto> getOotdThumbnails(
            @RequestParam(required = false, defaultValue = "NEWEST") OotdOrderType order,
            @RequestParam int page, @RequestParam int size) {
        return new ResponseDto<>(ootdService.getOotdThumbnailsByOrderType(page, size, order, 0L));
    }

    @Operation(summary = "OOTD 게시글 상세 조회", description = "OOTD 게시글 ID에 맞는 게시글을 상세하게 조회합니다.")
    @GetMapping("/{ootdId}")
    public ResponseDto<OotdDetailDto> getOotdDetailByOotdID(@PathVariable Long ootdId) {
        return new ResponseDto<>(ootdService.getOotdDetailByOotdId(ootdId, 0L));
    }

    @Operation(summary = "OOTD 게시글 좋아요 오청", description = "OOTD 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{ootdId}/like")
    public ResponseDto<OotdLikeDto> likeOotdByOotdId(@PathVariable Long ootdId) {
        return new ResponseDto<>(ootdService.sendLikeToOotd(ootdId, 0L));
    }

    @GetMapping("/{ootdId}/comments")
    @Operation(summary = "게시글 상세 댓글 조회", description = "OOTD 게시글 ID에 맞는 댓글들을 조회합니다.")
    public ResponseDto<CommentsDto> getComments(@PathVariable Long ootdId) {
        return new ResponseDto<>(ootdService.getCommentsByOotdId(ootdId));
    }

    @PostMapping("/{ootdId}/comments")
    @Operation(summary = "게시글 댓글 추가", description = "OOTD 게시글에 댓글을 답니다.")
    public ResponseDto<OotdCommentDto> writeComment(
            @PathVariable Long ootdId, @Validated @RequestBody OotdCommentRequestDto postComment) {
        return new ResponseDto<>(ootdService.writeCommentToOotd(ootdId, 0L,
                postComment.commentId(), postComment.comment()));
    }
}
