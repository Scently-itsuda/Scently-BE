package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum OotdOrderType {
    NEWEST("최신순"),
    POPULAR("인기순");

    private final String description;

    OotdOrderType(String description) {
        this.description = description;
    }
}
