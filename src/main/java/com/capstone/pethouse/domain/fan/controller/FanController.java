package com.capstone.pethouse.domain.fan.controller;

import com.capstone.pethouse.domain.fan.dto.response.FanScheduleResponse;
import com.capstone.pethouse.domain.fan.service.FanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/devices")
@RestController
public class FanController {

    private final FanService fanService;

    // 자동 FAN 스케줄러 가져오기
    @GetMapping("/{houseId}/fan/schedules")
    public Page<FanScheduleResponse> getFanSchedules(
            @PathVariable Long houseId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return fanService.getFanSchedules(houseId, pageable);
    }
}
