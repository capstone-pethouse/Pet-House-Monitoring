package com.capstone.pethouse.domain.fan.service;

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

    @Transactional(readOnly = true)
    public Page<FanScheduleResponse> getFanSchedules(Long houseId, Pageable pageable) {
        Page<FanSchedule> fanSchedulePage = fanScheduleRepository.findByPetHouse_HouseId(houseId, pageable);

        return fanSchedulePage.map(FanScheduleResponse::from);
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
