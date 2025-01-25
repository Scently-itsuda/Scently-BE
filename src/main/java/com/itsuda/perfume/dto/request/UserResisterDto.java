package com.itsuda.perfume.dto.request;

import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.util.ValidationUtil;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResisterDto(
    @Schema(description = "닉네임", example = "wqew1234")
    String nickname,

    @Schema(description = "성별", example = "MALE", allowableValues = {"MALE", "FEMALE", "UNKNOWN"})
    GenderType gender,

    @Schema(description = "생년월일", example = "2000-01-01", pattern = "yyyy-MM-dd")
    String birthDate 
) {
    public UserResisterDto {
        if (!ValidationUtil.isValidDateFormat(birthDate)) {
            throw new RestApiException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }
}
