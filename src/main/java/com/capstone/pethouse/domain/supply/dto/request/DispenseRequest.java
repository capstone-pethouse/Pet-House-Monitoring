package com.capstone.pethouse.domain.supply.dto.request;

import com.capstone.pethouse.domain.enums.FeedType;
import com.capstone.pethouse.domain.enums.UnitType;

import java.math.BigDecimal;

public record DispenseRequest(
        FeedType feedType,
        BigDecimal amount,
        UnitType unitType
) {

}
