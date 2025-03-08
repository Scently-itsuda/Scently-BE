package com.itsuda.perfume.controller;

import com.itsuda.perfume.dto.request.ootd.OotdMainRequestDto;
import com.itsuda.perfume.dto.response.ootd.OotdMainDto;
import com.itsuda.perfume.exception.ResponseDto;
import com.itsuda.perfume.service.OotdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ootds")
public class OotdController {

    private final OotdService ootdService;

    @GetMapping
    public ResponseDto<OotdMainDto> getOotds(@ModelAttribute OotdMainRequestDto ootdMainRequestDto) {
        return new ResponseDto<>(ootdService.getOotdThumbnailsBySort(ootdMainRequestDto.getPage(),
                ootdMainRequestDto.getSize(), ootdMainRequestDto.getOrder()));

    }
}
