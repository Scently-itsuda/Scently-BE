package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum PostOrderType {
    NEWEST_DESCENDING("최신순"),
    NEWEST_ASCENDING("역최신순"),
    POPULAR_DESCENDING("인기순"),
    POPULAR_ASCENDING("역인기순");

    private final String description;

    PostOrderType(String description) {
        this.description = description;
    }
}
