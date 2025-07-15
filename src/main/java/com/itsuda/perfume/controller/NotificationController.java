package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.dto.response.notification.UserNotificationsDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "알림 조회 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 모아보기", description = "소셜, 이벤트 전체 알림을 모아봅니다.")
    @GetMapping
    public ResponseDto<UserNotificationsDto> getAllUserNotifications(
            @UserId Long userId, @RequestParam int page, @RequestParam int size
    ) {
        return new ResponseDto<>(notificationService.getAllNotificationsByUserId(userId, page, size));
    }
}
