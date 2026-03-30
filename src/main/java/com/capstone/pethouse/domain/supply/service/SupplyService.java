package com.capstone.pethouse.domain.supply.service;

import com.capstone.pethouse.domain.device.entity.PetHouse;
import com.capstone.pethouse.domain.device.repository.PetHouseRepository;
import com.capstone.pethouse.domain.enums.TriggerType;
import com.capstone.pethouse.domain.supply.dto.request.SupplyLogRequest;
import com.capstone.pethouse.domain.supply.dto.request.ScheduleRequest;
import com.capstone.pethouse.domain.supply.dto.response.SupplyLogResponse;
import com.capstone.pethouse.domain.supply.dto.response.ScheduleResponse;
import com.capstone.pethouse.domain.supply.entity.SupplyLog;
import com.capstone.pethouse.domain.supply.entity.SupplySchedule;
import com.capstone.pethouse.domain.supply.repository.SupplyLogRepository;
import com.capstone.pethouse.domain.supply.repository.SupplyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SupplyService {

    private final SupplyLogRepository supplyLogRepository;
    private final PetHouseRepository petHouseRepository;
    private final SupplyScheduleRepository supplyScheduleRepository;

    public SupplyLogResponse recordSupplyLog(Long id, SupplyLogRequest supplyLogRequest) {
        PetHouse petHouse = petHouseRepository.getReferenceById(id);

        SupplyLog supplyLog;
        if (supplyLogRequest.triggerType() == TriggerType.MANUAL) {
            supplyLog = SupplyLog.ofManual(
                    petHouse,
                    supplyLogRequest.feedType(),
                    supplyLogRequest.unitType(),
                    supplyLogRequest.amount()
            );
        } else {
            SupplySchedule supplySchedule = supplyScheduleRepository.getReferenceById(supplyLogRequest.scheduleId());
            supplyLog = SupplyLog.ofScheduled(
                    supplySchedule,
                    petHouse,
                    supplyLogRequest.feedType(),
                    supplyLogRequest.unitType(),
                    supplyLogRequest.amount()
            );
        }
        return SupplyLogResponse.from(supplyLogRepository.save(supplyLog));
    }

    public ScheduleResponse postSchedule(Long id, ScheduleRequest scheduleRequest) {
        if (supplyScheduleRepository.existsByPetHouse_HouseIdAndFeedTypeAndCronExpression(id, scheduleRequest.feedType(), scheduleRequest.cronExpression())) {
            throw new IllegalStateException("이미 동일한 스케줄이 존재합니다.");
        }

        PetHouse petHouse = petHouseRepository.getReferenceById(id);
        SupplySchedule supplySchedule = SupplySchedule.of(
                petHouse, 
                scheduleRequest.feedType(), 
                scheduleRequest.unitType(), 
                scheduleRequest.amount(), 
                scheduleRequest.cronExpression()
        );

        return ScheduleResponse.from(supplyScheduleRepository.save(supplySchedule));
    }
}
