package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.Schedule;
import com.example.uchebapi.domain.ScheduleItem;
import com.example.uchebapi.dtos.GetSchedule;
import com.example.uchebapi.dtos.ReorderScheduleItemDto;
import com.example.uchebapi.dtos.ScheduleItemDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.ScheduleItemRepo;
import com.example.uchebapi.repos.ScheduleRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleService {

    private final ScheduleItemRepo scheduleItemRepo;
    private final MapperConfig mapper;
    private final ScheduleRepo scheduleRepo;
    private final AuthenticationService authenticationService;

    public ScheduleService(ScheduleItemRepo scheduleItemRepo, MapperConfig mapper, ScheduleRepo scheduleRepo, AuthenticationService authenticationService) {
        this.scheduleItemRepo = scheduleItemRepo;
        this.mapper = mapper;
        this.scheduleRepo = scheduleRepo;
        this.authenticationService = authenticationService;
    }

    private void updateScheduleVersion(UUID groupId) {
        Schedule scheduleVersion = scheduleRepo.findByGroupId(groupId);
        scheduleVersion.setScheduleVersion(scheduleVersion.getScheduleVersion() + 1);
        scheduleRepo.save(scheduleVersion);
    }

    public ScheduleItem createScheduleItem(ScheduleItem scheduleItem) {
        if (scheduleItemRepo.countByGroupIdAndWeekDayAndWeek(scheduleItem.getGroupId(), scheduleItem.getWeekDay(), scheduleItem.getWeek()) >= 10)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");

        updateScheduleVersion(scheduleItem.getGroupId());

        return scheduleItemRepo.save(scheduleItem);
    }

    public void deleteScheduleItem(UUID scheduleItemId) {
        Optional<ScheduleItem> scheduleItem = scheduleItemRepo.findById(scheduleItemId);
        if (scheduleItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule item not exists");

        scheduleItemRepo.delete(scheduleItem.get());

        updateScheduleVersion(scheduleItem.get().getGroupId());
    }

    public ScheduleItem updateScheduleItem(UUID scheduleItemId, ScheduleItemDto scheduleItem) {
        Optional<ScheduleItem> scheduleItemSource = scheduleItemRepo.findById(scheduleItemId);
        if (scheduleItemSource.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule item not exists");

        mapper.getMapper().map(scheduleItem, scheduleItemSource.get());
        if (scheduleItem.room() == null) {
            scheduleItemSource.get().setRoom(null);
        }
        if (scheduleItem.teacherId() == null) {
            scheduleItemSource.get().setTeacherId(null);
        }

        scheduleItemRepo.save(scheduleItemSource.get());
        updateScheduleVersion(scheduleItemSource.get().getGroupId());

        return scheduleItemSource.get();
    }

    public ResponseEntity<GetSchedule> getSchedule(UUID groupId, Integer userScheduleVersion) {
        Integer scheduleVersion = scheduleRepo.findByGroupId(groupId).getScheduleVersion();
        if (userScheduleVersion.equals(scheduleVersion))
            return new ResponseEntity<>(new GetSchedule(null, scheduleVersion), HttpStatus.NO_CONTENT);

        List<ScheduleItem> scheduleItems = scheduleItemRepo.findByGroupId(groupId);

        return new ResponseEntity<>(new GetSchedule(scheduleItems, scheduleVersion), HttpStatus.OK);
    }

    public void reorderScheduleItem(List<ReorderScheduleItemDto> reorderScheduleItemDtos) {
        if (reorderScheduleItemDtos.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "List is empty");

        Optional<ScheduleItem> scheduleItem = scheduleItemRepo.findById(reorderScheduleItemDtos.get(0).id());
        if (scheduleItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Schedule no longer exists");

        UUID groupId = scheduleItem.get().getGroupId();
        if (authenticationService.getPrivilegeLevel(groupId) > GroupRoles.Editor.ordinal())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner and editor can update schedule items");

        int tempOrder = -1;
        for (var toReorder : reorderScheduleItemDtos) {
            scheduleItemRepo.updateLessonOrderById(tempOrder--, toReorder.id());
        }

        for (var toReorder : reorderScheduleItemDtos) {
            scheduleItemRepo.updateLessonOrderById(toReorder.order(), toReorder.id());
        }

        updateScheduleVersion(groupId);
    }
}
