package com.capstone.pethouse.domain.enums;

import lombok.Getter;

@Getter
public enum FeedStatus {
    SUCCESS("성공"),
    FAIL("실패");

    private final String description;

    FeedStatus(String description) {
        this.description = description;
    }
}
