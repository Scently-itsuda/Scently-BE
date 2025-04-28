package com.itsuda.perfume.dto.request.fcm;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequestDto(
        @NotBlank(message = "FCM_TOKEN_INVALID_ERROR")
        String fcmToken
) {
}
