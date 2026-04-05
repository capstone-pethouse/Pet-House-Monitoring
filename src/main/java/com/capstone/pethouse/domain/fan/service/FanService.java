package com.capstone.pethouse.domain.fan.service;

import com.capstone.pethouse.domain.device.entity.PetHouse;
import com.capstone.pethouse.domain.device.repository.PetHouseRepository;
import com.capstone.pethouse.domain.fan.dto.request.FanScheduleRequest;
import com.capstone.pethouse.domain.fan.dto.response.FanScheduleResponse;
import com.capstone.pethouse.domain.fan.dto.response.FanToggleResponse;
import com.capstone.pethouse.domain.fan.entity.FanSchedule;
import com.capstone.pethouse.domain.fan.repository.FanScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FanService {

    private final FanScheduleRepository fanScheduleRepository;
    private final PetHouseRepository petHouseRepository;

    @Transactional(readOnly = true)
    public Page<FanScheduleResponse> getFanSchedules(Long houseId, Pageable pageable) {
        Page<FanSchedule> fanSchedulePage = fanScheduleRepository.findByPetHouse_HouseId(houseId, pageable);

        return fanSchedulePage.map(FanScheduleResponse::from);
    }

    public FanScheduleResponse postFanSchedule(Long houseId, FanScheduleRequest fanScheduleRequest) {
        if (fanScheduleRequest.endTime().isBefore(fanScheduleRequest.startTime())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        if (fanScheduleRepository.existingOverlappingSchedule(houseId, fanScheduleRequest.startTime(), fanScheduleRequest.endTime())) {
            throw new IllegalStateException("해당 시간대에 이미 겹치는 팬 스케줄이 존재합니다.");
        }

        PetHouse petHouse = petHouseRepository.getReferenceById(houseId);
        FanSchedule fanSchedule = FanSchedule.of(petHouse, fanScheduleRequest.startTime(), fanScheduleRequest.endTime());

        fanScheduleRequest.fanScheduleDetailRequestList()
                .forEach(request -> fanSchedule.addFanScheduleDetail(request.temperature(), request.speed()));

        return FanScheduleResponse.from(fanScheduleRepository.save(fanSchedule));
    }

    public FanToggleResponse toggleFanSchedule(Long houseId, Long scheduleId, boolean enabled) {
        FanSchedule fanSchedule = fanScheduleRepository.findByPetHouse_HouseIdAndId(houseId, scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 스케줄을 찾을 수 없습니다."));

        fanSchedule.toggleFanSchedule(enabled);

        return FanToggleResponse.from(fanSchedule);
    }

    public Long deleteFanSchedule(Long houseId, Long scheduleId) {
        FanSchedule fanSchedule = fanScheduleRepository.findByPetHouse_HouseIdAndId(houseId, scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("해당 스케줄을 찾을 수 없습니다."));

        fanScheduleRepository.delete(fanSchedule);

        return fanSchedule.getId();
    }
}
