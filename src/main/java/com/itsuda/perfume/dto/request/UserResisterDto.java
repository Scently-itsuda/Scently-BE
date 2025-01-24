package com.itsuda.perfume.dto.request;

import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.util.ValidationUtil;

public record UserResisterDto(
        GenderType gender,
        String birthDate,
        String nickname
) {
    public UserResisterDto {
        if (!ValidationUtil.isValidDateFormat(birthDate)) {
            throw new RestApiException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }
}
