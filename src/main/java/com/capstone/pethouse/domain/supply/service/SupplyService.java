package com.capstone.pethouse.domain.supply.service;

import com.capstone.pethouse.domain.device.entity.PetHouse;
import com.capstone.pethouse.domain.device.repository.PetHouseRepository;
import com.capstone.pethouse.domain.supply.dto.request.DispenseRequest;
import com.capstone.pethouse.domain.supply.dto.response.DispenseResponse;
import com.capstone.pethouse.domain.supply.entity.SupplyLog;
import com.capstone.pethouse.domain.supply.repository.SupplyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SupplyService {

    private final SupplyLogRepository supplyLogRepository;
    private final PetHouseRepository petHouseRepository;

    public DispenseResponse dispenseFood(Long id, DispenseRequest dispenseRequest) {
        PetHouse petHouse = petHouseRepository.getReferenceById(id);

        SupplyLog supplyLog = SupplyLog.ofManual(
                petHouse,
                dispenseRequest.feedType(),
                dispenseRequest.unitType(),
                dispenseRequest.amount()
        );

        return DispenseResponse.from(supplyLogRepository.save(supplyLog));
    }
}
