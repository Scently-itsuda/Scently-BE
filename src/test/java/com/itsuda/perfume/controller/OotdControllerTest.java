package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.request.ootd.OotdCommentRequestDto;
import com.itsuda.perfume.dto.response.ootd.CommentsDto;
import com.itsuda.perfume.dto.response.ootd.OotdDetailDto;
import com.itsuda.perfume.dto.response.ootd.OotdLikeDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.service.OotdService;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OotdController.class)
@WithMockUser
@ActiveProfiles("test")
class OotdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OotdService ootdService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("OOTD 썸네일들을 조회한다.")
    @Test
    void getOotdThumbnails() throws Exception {
        // given
        OotdMainDto result = new OotdMainDto(null, null);

        Mockito.when(ootdService.getOotdThumbnailsByOrderType(anyInt(), anyInt(), eq(OotdOrderType.NEWEST), anyLong()))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/ootds")
                                .queryParam("order", "NEWEST")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글 아이디를 기반으로 OOTD의 세부 내용과 이미지들을 조회한다.")
    @Test
    void getOotdDetail() throws Exception {
        // given
        OotdDetailDto result = new OotdDetailDto(null, null, null);

        Mockito.when(ootdService.getOotdDetailByOotdId(anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ootds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("자유게시판의 게시글 ID에 달린 댓글을 조회한다.")
    @Test
    void getComments() throws Exception {
        // given
        CommentsDto result = new CommentsDto(null, 0);
        Mockito.when(ootdService.getCommentsByOotdId(anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ootds/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글에 좋아요를 눌러서 좋아요를 요청한다.")
    @Test
    void sendLikeToOotd() throws Exception {
        // given
        OotdLikeDto result = new OotdLikeDto(null, null);

        Mockito.when(ootdService.sendLikeToOotd(anyLong(), anyLong())).thenReturn(result);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ootds/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("OOTD 게시글에 내용이 없는(공백으로 찬) 댓글은 달 수 없다.")
    @Test
    void replyWithNoContentsIsNotPermitted() throws Exception {
        // given
        OotdCommentRequestDto ootdComment = new OotdCommentRequestDto(null, "");

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ootds/1/comments").with(csrf())
                        .content(objectMapper.writeValueAsString(ootdComment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("댓글은 공백이 아닌 1자 이상이 포함되어야 합니다"));
    }
}