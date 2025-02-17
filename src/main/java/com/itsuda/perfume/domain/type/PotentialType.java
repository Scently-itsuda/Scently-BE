package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.Getter;

@Getter
public enum PotentialType {
    EDC("오 드 코롱"),
    EDT("오 드 뚜왈렛"),
    EDP("오 드 퍼퓸"),
    PERFUME("퍼퓸");
    
    private final String description;
    
    PotentialType(String description) {
        this.description = description;
    }

    public static PotentialType of(String description) {
        for (PotentialType potentialType : PotentialType.values()) {
            if (potentialType.getDescription().equals(description)) {
                return potentialType;
            }
        }
        throw new RestApiException(ErrorCode.INVALID_POTENTIAL_TYPE);
    }
} 