package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.dto.request.report.CommentReportDto;
import com.itsuda.perfume.dto.request.report.OotdReportDto;
import com.itsuda.perfume.dto.request.report.PostReportDto;
import com.itsuda.perfume.dto.request.report.ReviewReportDto;
import com.itsuda.perfume.dto.response.report.ReportedCommentDto;
import com.itsuda.perfume.dto.response.report.ReportedOotdDto;
import com.itsuda.perfume.dto.response.report.ReportedPostDto;
import com.itsuda.perfume.dto.response.report.ReportedReviewDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Report", description = "신고 관련 API")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "OOTD 신고", description = "OOTD 게시글을 신고합니다.")
    @PostMapping("/ootds/{ootdId}")
    public ResponseDto<ReportedOotdDto> reportOotdByOotdId(
            @RequestBody @Valid OotdReportDto ootdReportDto, @PathVariable Long ootdId, @UserId Long userId) {
        return new ResponseDto<>(reportService.reportOotdByOotdIdAndUserId(ootdReportDto, ootdId, userId));
    }

    @Operation(summary = "자유게시글 신고", description = "자유게시글을 신고합니다.")
    @PostMapping("/posts/{postId}")
    public ResponseDto<ReportedPostDto> reportPostByPostId(
            @RequestBody @Valid PostReportDto postReportDto, @PathVariable Long postId, @UserId Long userId) {
        return new ResponseDto<>(reportService.reportPostByPostIdAndUserId(postReportDto, postId, userId));
    }

    @Operation(summary = "댓글 신고", description = "댓글을 신고합니다.")
    @PostMapping("/comments/{commentId}")
    public ResponseDto<ReportedCommentDto> reportCommentByCommentId(
            @RequestBody @Valid CommentReportDto commentReportDto, @PathVariable Long commentId, @UserId Long userId
    ) {
        return new ResponseDto<>(reportService.reportCommentByCommentId(commentReportDto, commentId, userId));
    }

    @Operation(summary = "리뷰 신고", description = "리뷰를 신고합니다.")
    @PostMapping("/reviews/{reviewId}")
    public ResponseDto<ReportedReviewDto> reportReviewByReviewId(
            @RequestBody @Valid ReviewReportDto reviewReportDto, @PathVariable Long reviewId, @UserId Long userId
    ) {
        return new ResponseDto<>(reportService.reportReviewByReviewId(reviewReportDto, reviewId, userId));
    }
}
