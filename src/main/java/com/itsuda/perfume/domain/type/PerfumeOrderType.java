package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum PerfumeOrderType {
    REGISTERED_AT_DESCENDING("등록순"),
    REGISTERED_AT_ASCENDING("역등록순"),
    POPULAR_DESCENDING("인기순"),
    POPULAR_ASCENDING("역인기순");

    private final String description;

    PerfumeOrderType(String description) {
        this.description = description;
    }
}
