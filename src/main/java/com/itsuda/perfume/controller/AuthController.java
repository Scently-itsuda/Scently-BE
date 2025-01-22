package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.dto.request.UserResisterDto;
import com.itsuda.perfume.dto.response.JwtTokenDto;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.security.Constants;
import com.itsuda.perfume.service.AuthService;
import com.itsuda.perfume.util.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseDto<JwtTokenDto> reissue(final HttpServletRequest request) {
        final String refreshToken = HeaderUtil.refineHeader(request, Constants.AUTHORIZATION_HEADER, Constants.BEARER_PREFIX)
                .orElseThrow(() -> new RestApiException(ErrorCode.INVALID_HEADER));

        final JwtTokenDto jwtTokenDto = authService.reissue(refreshToken);
        return new ResponseDto<>(jwtTokenDto);
    }

    //소셜 로그인 사용자 정보 등록
    @PatchMapping("/resister")
    public ResponseDto<?> resister(@UserId Long id, @RequestBody UserResisterDto requestDto) {
        return new ResponseDto<>(authService.registerUserInfo(id, requestDto));
    }
}