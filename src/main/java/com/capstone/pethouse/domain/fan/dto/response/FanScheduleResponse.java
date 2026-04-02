package com.capstone.pethouse.domain.fan.dto.response;

import com.capstone.pethouse.domain.fan.entity.FanSchedule;

import java.math.BigDecimal;
import java.time.LocalTime;

public record FanScheduleResponse(
        Long houseId,
        Long scheduleId,
        BigDecimal temperature,
        Integer speed,
        LocalTime startTime,
        LocalTime endTime
) {

    public static FanScheduleResponse from(FanSchedule fanSchedule) {
        return new FanScheduleResponse(
                fanSchedule.getPetHouse().getHouseId(),
                fanSchedule.getId(),
                fanSchedule.getTemperature(),
                fanSchedule.getSpeed(),
                fanSchedule.getStartTime(),
                fanSchedule.getEndTime()
        );
    }
}
