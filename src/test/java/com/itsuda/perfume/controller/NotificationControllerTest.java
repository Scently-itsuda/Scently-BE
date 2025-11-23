package com.itsuda.perfume.controller;

import com.itsuda.perfume.dto.response.notification.UserNotificationsDto;
import com.itsuda.perfume.service.NotificationService;
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

@WebMvcTest(NotificationController.class)
@WithMockUser
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @DisplayName("사용자의 모든 알림들을 조회한다.")
    @Test
    void getAllUserNotifications() throws Exception {
        // given
        UserNotificationsDto userNotificationsDto = new UserNotificationsDto(null, null);

        Mockito.when(notificationService.getAllNotificationsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(userNotificationsDto);

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/notifications")
                                .queryParam("uesrId", "0")
                                .queryParam("page", "0")
                                .queryParam("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("1200"));
    }
}