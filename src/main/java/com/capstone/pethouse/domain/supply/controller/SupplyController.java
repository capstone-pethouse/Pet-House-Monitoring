package com.capstone.pethouse.domain.supply.controller;

import com.capstone.pethouse.domain.supply.dto.request.DispenseRequest;
import com.capstone.pethouse.domain.supply.dto.request.ScheduleRequest;
import com.capstone.pethouse.domain.supply.dto.response.DispenseResponse;
import com.capstone.pethouse.domain.supply.dto.response.ScheduleResponse;
import com.capstone.pethouse.domain.supply.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class SupplyController {

    private final SupplyService supplyService;

    // 수동 급수 / 급식 제어
    @PostMapping("/{id}/supplier/dispense")
    public DispenseResponse dispenseFood(
            @PathVariable Long id,                          // pet house의 아이디
            @RequestBody DispenseRequest dispenseRequest
    ) {
        return supplyService.dispenseFood(id, dispenseRequest);
    }

    // 자동 급수 / 급식 제어
    @PostMapping("/{id}/supplier/schedules")
    public ScheduleResponse postSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        return supplyService.postSchedule(id, scheduleRequest);
    }
}
