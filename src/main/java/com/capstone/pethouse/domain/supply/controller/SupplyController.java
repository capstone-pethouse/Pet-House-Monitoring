package com.capstone.pethouse.domain.supply.controller;

import com.capstone.pethouse.domain.supply.dto.request.SupplyLogRequest;
import com.capstone.pethouse.domain.supply.dto.request.ScheduleRequest;
import com.capstone.pethouse.domain.supply.dto.response.SupplyLogResponse;
import com.capstone.pethouse.domain.supply.dto.response.ScheduleResponse;
import com.capstone.pethouse.domain.supply.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class SupplyController {

    private final SupplyService supplyService;

    // 자동 급수 / 급식 스케줄러 등록
    @PostMapping("/{id}/supplier/schedules")
    public ScheduleResponse postSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        return supplyService.postSchedule(id, scheduleRequest);
    }

    // 제공된 급식 / 급수에 대한 supply_log 저장
    @PostMapping("/{id}/supplier/record")
    public SupplyLogResponse recordSupplyLog(
            @PathVariable Long id,                          // pet house의 아이디
            @RequestBody SupplyLogRequest supplyLogRequest
    ) {
        return supplyService.recordSupplyLog(id, supplyLogRequest);
    }
}
