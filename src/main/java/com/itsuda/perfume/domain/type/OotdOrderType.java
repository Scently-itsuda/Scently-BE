package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum OotdOrderType {
    NEWEST_DESCENDING("최신순"),
    NEWEST_ASCENDING("역최신순"),
    POPULAR_DESCENDING("인기순"),
    POPULAR_ASCENDING("역인기순");

    private final String description;

    OotdOrderType(String description) {
        this.description = description;
    }
}
