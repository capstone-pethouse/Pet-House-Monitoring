package com.capstone.pethouse.domain.fan.repository.querydsl;

import java.time.LocalTime;

public interface FanScheduleRepositoryCustom {
    boolean existingOverlappingSchedule(Long houseId, LocalTime startTime, LocalTime endTime);
}
