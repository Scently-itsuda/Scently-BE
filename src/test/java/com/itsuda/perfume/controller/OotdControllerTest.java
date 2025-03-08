package com.itsuda.perfume.controller;

import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.service.OotdService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OotdController.class)
@WithMockUser
class OotdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OotdService ootdService;

    @DisplayName("OOTD 썸네일들을 조회한다.")
    @Test
    void getOotdThumbnails() throws Exception {
        // given
        OotdMainDto result = new OotdMainDto(null, null);

        Mockito.when(ootdService.getOotdThumbnailsBySort(0, 0, OotdOrderType.NEWEST)).thenReturn(result);

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
}