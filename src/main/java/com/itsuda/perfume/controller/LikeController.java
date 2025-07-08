package com.itsuda.perfume.controller;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.domain.type.OotdOrderType;
import com.itsuda.perfume.dto.response.ootd.UserLikeOotdsDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.OotdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
@Tag(name = "Like", description = "좋아요 모아보기 관련 API")
public class LikeController {

    private final OotdService ootdService;

    @Operation(summary = "OOTD 좋아요 모아보기", description = "좋아요를 누른 OOTD들을 순서에 맞게 조회합니다.")
    @GetMapping("/ootds")
    public ResponseDto<UserLikeOotdsDto> getUserLikeOotds(
            @UserId Long userId,
            @RequestParam(required = false, defaultValue = "NEWEST_DESCENDING") OotdOrderType order,
            @RequestParam int page, @RequestParam int size) {
        return new ResponseDto<>(ootdService.getAllUserLikeOotdsByOrderType(page, size, order, 0L));
    }
}
