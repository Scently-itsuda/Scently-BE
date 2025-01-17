package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import java.util.Arrays;

public enum EProvider {
    GOOGLE,
    NAVER,
    KAKAO;

    public static EProvider of(String provider) {
        return Arrays.stream(EProvider.values())
                .filter(v -> v.name().equals(provider))
                .findAny()
                .orElseThrow(() -> new RestApiException(ErrorCode.ACCESS_DENIED_ERROR));
    }
}
