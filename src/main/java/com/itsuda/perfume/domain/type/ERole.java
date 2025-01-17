package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ERole {
    GUEST("ROLE_GUEST", "GUEST"), USER("ROLE_USER", "USER"), ADMIN("ROLE_ADMIN", "ADMIN");

    private final String roleCode;
    private final String displayName;

    public static ERole of(String roleCode) {
        return Arrays.stream(ERole.values())
                .filter(v -> v.getRoleCode().equals(roleCode))
                .findAny()
                .orElseThrow(() -> new RestApiException(ErrorCode.ACCESS_DENIED_ERROR));
    }
}
