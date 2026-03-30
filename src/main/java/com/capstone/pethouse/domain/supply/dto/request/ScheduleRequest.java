package com.capstone.pethouse.domain.supply.dto.request;

import java.math.BigDecimal;

import com.capstone.pethouse.domain.enums.FeedType;
import com.capstone.pethouse.domain.enums.UnitType;

public record ScheduleRequest(
        FeedType feedType,
        UnitType unitType,
        BigDecimal amount,
        String cronExpression,
        boolean enabled
) {
    
}
