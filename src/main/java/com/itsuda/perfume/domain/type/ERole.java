package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ERole {
    GUEST, USER, ADMIN;

    public static ERole of(String role) {
        return Arrays.stream(ERole.values())
                .filter(v -> v.toString().equals(role))
                .findAny()
                .orElseThrow(() -> new RestApiException(ErrorCode.ACCESS_DENIED_ERROR));
    }
}