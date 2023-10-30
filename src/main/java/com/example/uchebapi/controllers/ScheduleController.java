package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.ScheduleItem;
import com.example.uchebapi.dtos.GetSchedule;
import com.example.uchebapi.dtos.ReorderScheduleItemDto;
import com.example.uchebapi.dtos.ScheduleItemDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.LearningGroupRepo;
import com.example.uchebapi.repos.ScheduleItemRepo;
import com.example.uchebapi.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/schedule")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "Creates schedule item")
    @PostMapping("create")
    @AccessControl(fromRepo = ScheduleItemRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public ScheduleItem createScheduleItem(@RequestBody ScheduleItem scheduleItem) {
        return scheduleService.createScheduleItem(scheduleItem);
    }

    @Operation(summary = "Deletes schedule item")
    @DeleteMapping("delete/{scheduleItemId}")
    @AccessControl(fromRepo = ScheduleItemRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public void deleteScheduleItem(@PathVariable UUID scheduleItemId) {
        scheduleService.deleteScheduleItem(scheduleItemId);
    }

//    @Operation(summary = "Gets schedule item")
//    @GetMapping("get/{scheduleItemId}")
//    public ScheduleItem getScheduleItem(@PathVariable UUID scheduleItemId) {
//        return scheduleService.getScheduleItem(scheduleItemId);
//    }

    @Operation(summary = "Updates schedule item")
    @PutMapping("update/{scheduleItemId}")
    @AccessControl(fromRepo = ScheduleItemRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public ScheduleItem updateScheduleItem(@PathVariable UUID scheduleItemId,
                                           @RequestBody ScheduleItemDto scheduleItem) {
        return scheduleService.updateScheduleItem(scheduleItemId, scheduleItem);
    }

    @Operation(summary = "Get schedule")
    @GetMapping("getSchedule/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public ResponseEntity<GetSchedule> getSchedule(@PathVariable UUID groupId,
                                                   @RequestParam Integer scheduleVersion) {
        return scheduleService.getSchedule(groupId, scheduleVersion);
    }

    @Operation(summary = "Reorders schedule items")
    @PutMapping("reorder")
    public void reorderScheduleItem(@RequestBody List<ReorderScheduleItemDto> reorderScheduleItemDtos) {
        scheduleService.reorderScheduleItem(reorderScheduleItemDtos);
    }
}
