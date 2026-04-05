package com.capstone.pethouse.domain.fan.repository.querydsl;

import static com.capstone.pethouse.domain.fan.entity.QFanSchedule.fanSchedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
public class FanScheduleRepositoryImpl implements FanScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existingOverlappingSchedule(Long houseId, LocalTime startTime, LocalTime endTime) {
        Integer selectOne = queryFactory
                .selectOne()
                .from(fanSchedule)
                .where(
                        houseIdEq(houseId),
                        startTimeBefore(endTime),
                        endTimeAfter(startTime)
                )
                .fetchFirst();

        return selectOne != null;
    }

    private BooleanExpression houseIdEq(Long houseId) {
        return fanSchedule.petHouse.houseId.eq(houseId);
    }

    private BooleanExpression startTimeBefore(LocalTime endTime) {
        return fanSchedule.startTime.lt(endTime);                   // 새 endTime > 기존 startTime
    }

    private BooleanExpression endTimeAfter(LocalTime startTime) {
        return fanSchedule.endTime.gt(startTime);                   // 새 startTime < 기존 endTime
    }
}
