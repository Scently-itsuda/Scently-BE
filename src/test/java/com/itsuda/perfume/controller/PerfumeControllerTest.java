package com.itsuda.perfume.controller;

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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerfumeController.class)
@WithMockUser
@ActiveProfiles("test")
class PerfumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerfumeService perfumeService;

    @DisplayName("특정 향수를 위시에 담는다.")
    @Test
    void sendWishToPerfume() throws Exception {
        // given
        Mockito.doNothing().when(perfumeService).sendWishToPerfume(anyLong(), anyLong());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/perfumes/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}