package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.Getter;

@Getter
public enum GenderType {
    MALE("남성"),
    FEMALE("여성"),
    UNISEX("유니섹스"),
    UNKNOWN("알 수 없음");
    
    private final String description;
    
    GenderType(String description) {
        this.description = description;
    }

    public static GenderType of(String gender) {
        for (GenderType genderType : GenderType.values()) {
            if (genderType.getDescription().equals(gender)) {
                return genderType;
            }
        }
        throw new RestApiException(ErrorCode.INVALID_GENDER_TYPE);
    }
}
