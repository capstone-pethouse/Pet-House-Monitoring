package com.capstone.pethouse.domain.supply.dto.response;

import com.capstone.pethouse.domain.supply.entity.SupplySchedule;

public record ScheduleToggleResponse(
        Long houseId,
        Long scheduleId,
        boolean enabled
) {
    public static ScheduleToggleResponse from(SupplySchedule supplySchedule) {
        return new ScheduleToggleResponse(
                supplySchedule.getPetHouse().getHouseId(),
                supplySchedule.getId(),
                supplySchedule.isEnabled()
        );
    }
}
