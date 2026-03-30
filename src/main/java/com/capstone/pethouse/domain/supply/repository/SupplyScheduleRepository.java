package com.capstone.pethouse.domain.supply.repository;

import com.capstone.pethouse.domain.enums.FeedType;
import com.capstone.pethouse.domain.supply.entity.SupplySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyScheduleRepository extends JpaRepository<SupplySchedule, Long> {
    boolean existsByPetHouse_HouseIdAndFeedTypeAndCronExpression(Long houseId, FeedType feedType, String cronExpression);
}
