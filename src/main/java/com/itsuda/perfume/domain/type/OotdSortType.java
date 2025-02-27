package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.Getter;

@Getter
public enum OotdSortType {
    POPULAR("인기순");

    private final String description;

    OotdSortType(String description) {
        this.description = description;
    }

    public static OotdSortType of(String description) {
        for (OotdSortType ootdSortType : OotdSortType.values()) {
            if (ootdSortType.getDescription().equals(description)) {
                return ootdSortType;
            }
        }
        throw new RestApiException(ErrorCode.INVALID_OOTD_SORT_TYPE);
    }
}
