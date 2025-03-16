package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum PostOrderType {
    NEWEST("최신순");

    private final String description;

    PostOrderType(String description) {
        this.description = description;
    }
}
