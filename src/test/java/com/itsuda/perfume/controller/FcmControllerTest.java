package com.itsuda.perfume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itsuda.perfume.dto.request.fcm.FcmTokenRequestDto;
import com.itsuda.perfume.service.FcmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FcmController.class)
@WithMockUser
@ActiveProfiles("test")
class FcmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FcmService fcmService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("FCM 토큰을 저장한다.")
    @Test
    void saveUserFcmToken() throws Exception {
        // given
        FcmTokenRequestDto fcmTokenRequestDto = new FcmTokenRequestDto("thisistesttoken");

        Mockito.doNothing().when(fcmService).saveUserFcmToken(anyLong(), anyString());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/fcm/token").with(csrf())
                        .content(objectMapper.writeValueAsString(fcmTokenRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1200"))
                .andExpect(jsonPath("$.data.isSaved").value("true"));
    }

    @DisplayName("FCM 토큰은 비어있으면 안된다.")
    @ValueSource(strings = {"", " "})
    @ParameterizedTest
    void uesrFcmTokenIsNotBlank(String fcmToken) throws Exception {
        // given
        FcmTokenRequestDto fcmTokenRequestDto = new FcmTokenRequestDto(fcmToken);

        Mockito.doNothing().when(fcmService).saveUserFcmToken(anyLong(), anyString());

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/fcm/token").with(csrf())
                        .content(objectMapper.writeValueAsString(fcmTokenRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value("1400"))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("$.message").value("FCM 토큰 형식이 올바르지 않습니다"));
    }
}