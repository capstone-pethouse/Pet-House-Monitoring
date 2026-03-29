package com.capstone.pethouse.domain.device.repository;

import com.capstone.pethouse.domain.device.entity.PetHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetHouseRepository extends JpaRepository<PetHouse, Long> {
}
