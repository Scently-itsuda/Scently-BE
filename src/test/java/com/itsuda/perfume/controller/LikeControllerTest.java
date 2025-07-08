package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.domain.type.PerfumeOrderType;
import com.itsuda.perfume.dto.response.like.WishPerfumesDto;
import com.itsuda.perfume.dto.response.ootd.UserLikeOotdsDto;
import com.itsuda.perfume.service.OotdService;
import com.itsuda.perfume.service.PerfumeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LikeController.class)
@WithMockUser
@ActiveProfiles("test")
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OotdService ootdService;

    @MockBean
    private PerfumeService perfumeService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("좋아요를 누른 OOTD 목록들을 조회한다.")
    @Test
    void getUserLikeOotds() throws Exception {
        // given
        UserLikeOotdsDto result = new UserLikeOotdsDto(null, null);

        Mockito.when(ootdService.getAllUserLikeOotdsByOrderType(anyInt(), anyInt(), eq(OotdOrderType.NEWEST_DESCENDING), anyLong()))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/likes/ootds")
                                .queryParam("order", "NEWEST_DESCENDING")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }

    @DisplayName("위시리스트에 담은 향수 목록들을 조회한다.")
    @Test
    void getAllWishPerfumes() throws Exception {
        // given
        WishPerfumesDto result = new WishPerfumesDto(null, null);

        Mockito.when(perfumeService.getAllWishPerfumes(any(), anyInt(), anyInt(), eq(PerfumeOrderType.REGISTERED_AT_DESCENDING), anyLong()))
                .thenReturn(result);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/likes/perfumes")
                                .queryParam("order", "REGISTERED_AT_DESCENDING")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}