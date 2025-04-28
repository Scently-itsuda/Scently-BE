package com.itsuda.perfume.controller;

import com.itsuda.perfume.dto.request.fcm.FcmTokenRequestDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "FCM", description = "FCM 토큰 등록 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    @PutMapping("/token")
    public ResponseDto<Map<String, Boolean>> saveUserFcmToken(@Valid @RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        fcmService.saveUserFcmToken(0L, fcmTokenRequestDto.fcmToken());
        return new ResponseDto<>(Map.of("isSaved", Boolean.TRUE));
    }
}
