package com.capstone.pethouse.domain.supply.controller;

import com.capstone.pethouse.domain.supply.dto.request.SupplyLogRequest;
import com.capstone.pethouse.domain.supply.dto.request.ScheduleRequest;
import com.capstone.pethouse.domain.supply.dto.response.ScheduleToggleResponse;
import com.capstone.pethouse.domain.supply.dto.response.SupplyLogHistoryResponse;
import com.capstone.pethouse.domain.supply.dto.response.SupplyLogResponse;
import com.capstone.pethouse.domain.supply.dto.response.ScheduleResponse;
import com.capstone.pethouse.domain.supply.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class SupplyController {

    private final SupplyService supplyService;

    // 자동 급수 / 급식 스케줄러 가져오기
    @GetMapping("/{houseId}/supplier/schedules")
    public Page<ScheduleResponse> getSchedules(
            @PathVariable Long houseId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return supplyService.getSchedules(houseId, pageable);
    }

    // 자동 급수 / 급식 스케줄러 등록
    @PostMapping("/{houseId}/supplier/schedules")
    public ScheduleResponse postSchedule(
            @PathVariable Long houseId,
            @Valid @RequestBody ScheduleRequest scheduleRequest
    ) {
        return supplyService.postSchedule(houseId, scheduleRequest);
    }

    // 자동 급수 / 급식 스케줄러 수정
    @PutMapping("/{houseId}/supplier/schedules/{scheduleId}")
    public ScheduleResponse updateSchedule(
            @PathVariable Long houseId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest scheduleRequest
    ) {
        return supplyService.updateSchedule(houseId, scheduleId, scheduleRequest);
    }

    // 자동 급수 / 급식 스케줄러 활성, 비활성 토글
    @PatchMapping("/{houseId}/supplier/schedules/{scheduleId}/toggle")
    public ScheduleToggleResponse toggleSchedule(
            @PathVariable Long houseId,
            @PathVariable Long scheduleId,
            @RequestParam boolean enabled
    ) {
        return supplyService.toggleSchedule(houseId, scheduleId, enabled);
    }

    // 자동 급수 / 급식 스케줄러 삭제
    @DeleteMapping("/{houseId}/supplier/schedules/{scheduleId}")
    public Long deleteSchedule(
            @PathVariable Long houseId,
            @PathVariable Long scheduleId
    ) {
        return supplyService.deleteSchedule(houseId, scheduleId);
    }

    // 제공된 급식 / 급수에 대한 supply_log 저장 (수동 제공시)
    // 자동 급식 / 급수 제공 시엔 MQTT 리스너가 서비스 호출
    @PostMapping("/{houseId}/supplier/record")
    public SupplyLogResponse recordSupplyLog(
            @PathVariable Long houseId,                          // pet house의 아이디
            @RequestBody SupplyLogRequest supplyLogRequest
    ) {
        return supplyService.recordSupplyLog(houseId, supplyLogRequest);
    }

    // 실제로 제공된 급식 / 급수에 대한 supply_log 보여주기
    @GetMapping("/{houseId}/supplier/record")
    public Page<SupplyLogHistoryResponse> getSupplyHistory(
            @PathVariable Long houseId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return supplyService.getSupplyHistory(houseId, pageable);
    }
}
