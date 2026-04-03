package com.capstone.pethouse.domain.fan.repository;

import com.capstone.pethouse.domain.fan.entity.FanSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FanScheduleRepository extends JpaRepository<FanSchedule, Long> {
    Page<FanSchedule> findByPetHouse_HouseId(Long houseId, Pageable pageable);
    Optional<FanSchedule> findByPetHouse_HouseIdAndId(Long houseId, Long scheduleId);
}
