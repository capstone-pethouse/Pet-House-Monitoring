package com.capstone.pethouse.domain.supply.entity;

import lombok.Getter;

@Getter
public enum FeedType {
    FOOD("급식"),
    WATER("급수");

    private final String description;

    FeedType(String description) {
        this.description = description;
    }
}
