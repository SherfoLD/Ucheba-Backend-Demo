package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.ScheduleConfig;
import com.example.uchebapi.dtos.ScheduleConfigDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.LearningGroupRepo;
import com.example.uchebapi.repos.ScheduleConfigRepo;
import com.example.uchebapi.services.ScheduleConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/schedule/config")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Schedule Config")
public class ScheduleConfigController {

    private final ScheduleConfigService scheduleConfigService;

    public ScheduleConfigController(ScheduleConfigService scheduleConfigService) {
        this.scheduleConfigService = scheduleConfigService;
    }

    @Operation(summary = "Updates schedule config")
    @PutMapping("update/{scheduleConfigId}")
    @AccessControl(fromRepo = ScheduleConfigRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public ScheduleConfig updateScheduleConfig(@PathVariable UUID scheduleConfigId,
                              @RequestBody ScheduleConfigDto scheduleConfig) {
        return scheduleConfigService.updateScheduleConfig(scheduleConfigId, scheduleConfig); // TODO: Sanitize update with checks
    }
}
