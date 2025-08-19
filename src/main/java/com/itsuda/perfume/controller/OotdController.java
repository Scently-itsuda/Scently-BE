package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.request.ootd.CreateOotdDto;
import com.itsuda.perfume.dto.request.ootd.ImageFilesDto;
import com.itsuda.perfume.dto.request.ootd.OotdCommentRequestDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.CreatedOotdDto;
import com.itsuda.perfume.dto.response.ootd.OotdCommentDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.dto.response.perfume.OotdPerfumesDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.OotdService;
import com.itsuda.perfume.service.PerfumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ootds")
@Tag(name = "Ootd", description = "OOTD 게시글 관련 API")
public class OotdController {

    private final OotdService ootdService;
    private final PerfumeService perfumeService;

    @Operation(summary = "OOTD 목록 조회", description = "OOTD들을 정렬 순서에 맞게 조회합니다.")
    @GetMapping
    public ResponseDto<OotdMainDto> getOotdThumbnails(
            @UserId(required = false) Long userId, @RequestParam int page, @RequestParam int size,
            @RequestParam(required = false, defaultValue = "NEWEST_DESCENDING") OotdOrderType order
    ) {
        return new ResponseDto<>(ootdService.getOotdThumbnailsByOrderType(page, size, order, userId));
    }

    @Operation(summary = "OOTD 작성", description = "OOTD에 게시글을 작성합니다.")
    @PostMapping
    public ResponseDto<CreatedOotdDto> createOotd(
            @UserId Long userId, @Valid @RequestPart CreateOotdDto createOotdDto,
            @Valid @ModelAttribute ImageFilesDto imageFilesDto
    ) {
        return new ResponseDto<>(ootdService.createOotd(1L, createOotdDto.content(), createOotdDto.tagNames(),
                createOotdDto.volume(), createOotdDto.perfumeIds(), imageFilesDto.images()));
    }

    @Operation(summary = "OOTD 향수 조회", description = "OOTD 작성 중 모든 향수 목록을 조회합니다.")
    @GetMapping("/perfumes")
    public ResponseDto<OotdPerfumesDto> getOotdPerfumes() {
        return new ResponseDto<>(perfumeService.getAllPerfumes());
    }

    @Operation(summary = "OOTD 상세 조회", description = "OOTD ID에 맞는 게시글을 상세하게 조회합니다.")
    @GetMapping("/{ootdId}")
    public ResponseDto<OotdDetailDto> getOotdDetailByOotdID(@UserId(required = false) Long userId,
                                                            @PathVariable Long ootdId) {
        return new ResponseDto<>(ootdService.getOotdDetailByOotdId(ootdId, userId));
    }

    @Operation(summary = "OOTD 삭제", description = "OOTD ID에 맞는 게시글을 삭제합니다.")
    @DeleteMapping("/{ootdId}")
    public ResponseDto<Void> deleteOotdByOotdId(@UserId Long userId, @PathVariable Long ootdId) {
        ootdService.deleteOotdByOotdId(ootdId, userId);
        return new ResponseDto<>(null);
    }

    @Operation(summary = "OOTD 좋아요", description = "OOTD 게시글에 좋아요를 요청합니다.")
    @PostMapping("/{ootdId}/like")
    public ResponseDto<Void> likeOotdByOotdId(@UserId Long userId, @PathVariable Long ootdId) {
        ootdService.sendLikeToOotd(ootdId, userId);
        return new ResponseDto<>(null);
    }

    @Operation(summary = "OOTD 상세 댓글 조회", description = "OOTD 게시글 ID에 맞는 댓글들을 조회합니다.")
    @GetMapping("/{ootdId}/comments")
    public ResponseDto<CommentsDto> getComments(@PathVariable Long ootdId) {
        return new ResponseDto<>(ootdService.getCommentsByOotdId(ootdId));
    }

    @Operation(summary = "OOTD 댓글 작성", description = "OOTD 게시글에 댓글을 답니다.")
    @PostMapping("/{ootdId}/comments")
    public ResponseDto<OotdCommentDto> writeComment(
            @UserId Long userId,
            @PathVariable Long ootdId, @Validated @RequestBody OotdCommentRequestDto postComment) {
        return new ResponseDto<>(ootdService.writeCommentToOotd(ootdId, userId,
                postComment.commentId(), postComment.comment()));
    }

    @Operation(summary = "OOTD 댓글 삭제", description = "OOTD 댓글을 삭제합니다.")
    @DeleteMapping("/{ootdId}/comments/{commentId}")
    public ResponseDto<Void> deleteOotdCommentByCommentId(@UserId Long userId, @PathVariable Long commentId) {
        ootdService.deleteOotdComment(userId, commentId);
        return new ResponseDto<>(null);
    }

    @Operation(summary = "OOTD 댓글 좋아요", description = "OOTD 게시글의 댓글에 좋아요를 요청합니다.")
    @PostMapping("/{ootdId}/comments/{commentId}/like")
    public ResponseDto<Void> likeOotdCommentByCommentId(@UserId Long userId, @PathVariable Long commentId) {
        ootdService.sendLikeToOotdComment(userId, commentId);
        return new ResponseDto<>(null);
    }
}
