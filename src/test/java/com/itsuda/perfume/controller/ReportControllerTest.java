package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.domain.type.ReportType;
import com.itsuda.perfume.dto.request.report.CommentReportDto;
import com.itsuda.perfume.dto.request.report.OotdReportDto;
import com.itsuda.perfume.dto.request.report.PostReportDto;
import com.itsuda.perfume.dto.request.report.ReviewReportDto;
import com.itsuda.perfume.dto.response.report.ReportedCommentDto;
import com.itsuda.perfume.dto.response.report.ReportedOotdDto;
import com.itsuda.perfume.dto.response.report.ReportedPostDto;
import com.itsuda.perfume.dto.response.report.ReportedReviewDto;
import com.itsuda.perfume.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ReportController.class)
@WithMockUser
@ActiveProfiles("test")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @DisplayName("OOTD를 신고할 수 있다.")
    @Test
    void reportOotdByOotdId() throws Exception {
        // given
        OotdReportDto request = new OotdReportDto(ReportType.SPAM_AD, "스팸이네요");
        ReportedOotdDto result = new ReportedOotdDto(null);
        Mockito.when(reportService.reportOotdByOotdIdAndUserId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/ootds/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 신고 사유가 반드시 있어야 한다.")
    @Test
    void ootdReportMustHaveReportType() throws Exception {
        // given
        OotdReportDto request = new OotdReportDto(null, "스팸이네요");
        ReportedOotdDto result = new ReportedOotdDto(null);
        Mockito.when(reportService.reportOotdByOotdIdAndUserId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/ootds/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("신고 사유가 있어야 합니다"));
    }

    @DisplayName("자유게시글을 신고할 수 있다.")
    @Test
    void reportPostByPostId() throws Exception {
        // given
        PostReportDto request = new PostReportDto(ReportType.SPAM_AD, "스팸이네요");
        ReportedPostDto result = new ReportedPostDto(null);
        Mockito.when(reportService.reportPostByPostIdAndUserId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/posts/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시글 신고 사유가 반드시 있어야 한다.")
    @Test
    void postReportMustHaveReportType() throws Exception {
        // given
        PostReportDto request = new PostReportDto(null, "스팸이네요");
        ReportedPostDto result = new ReportedPostDto(null);
        Mockito.when(reportService.reportPostByPostIdAndUserId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/posts/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("신고 사유가 있어야 합니다"));
    }

    @DisplayName("댓글을 신고할 수 있다.")
    @Test
    void reportCommentByCommentId() throws Exception {
        // given
        CommentReportDto request = new CommentReportDto(ReportType.SPAM_AD, "스팸이네요");
        ReportedCommentDto result = new ReportedCommentDto(null);
        Mockito.when(reportService.reportCommentByCommentId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/comments/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("댓글 신고 사유가 반드시 있어야 한다.")
    @Test
    void commentReportMustHaveReportType() throws Exception {
        // given
        CommentReportDto request = new CommentReportDto(null, "스팸이네요");
        ReportedCommentDto result = new ReportedCommentDto(null);
        Mockito.when(reportService.reportCommentByCommentId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/comments/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("신고 사유가 있어야 합니다"));
    }

    @DisplayName("리뷰를 신고할 수 있다.")
    @Test
    void reportReviewByReviewId() throws Exception {
        // given
        ReviewReportDto request = new ReviewReportDto(ReportType.SPAM_AD, "스팸이네요");
        ReportedReviewDto result = new ReportedReviewDto(null);
        Mockito.when(reportService.reportReviewByReviewId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/reviews/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("리뷰 신고 사유가 반드시 있어야 한다.")
    @Test
    void reviewReportMustHaveReportType() throws Exception {
        // given
        ReviewReportDto request = new ReviewReportDto(null, "스팸이네요");
        ReportedReviewDto result = new ReportedReviewDto(null);
        Mockito.when(reportService.reportReviewByReviewId(any(), anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reports/comments/1").with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("신고 사유가 있어야 합니다"));
    }
}