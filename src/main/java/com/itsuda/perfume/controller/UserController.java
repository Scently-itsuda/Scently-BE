package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.ValidNickname;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "닉네임 중복 검사", description = "입력한 닉네임의 중복 여부를 확인합니다.")
    @GetMapping("/nickname/{nickname}/exists")
    public ResponseDto<Boolean> checkNicknameExists(
            @ValidNickname
            @PathVariable String nickname) {
        return new ResponseDto<>(userService.isNicknameExists(nickname));
    }
}
