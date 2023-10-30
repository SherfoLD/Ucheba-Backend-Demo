package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.ScheduleConfig;
import com.example.uchebapi.dtos.ScheduleConfigDto;
import com.example.uchebapi.repos.ScheduleConfigRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleConfigService {

    private final ScheduleConfigRepo scheduleConfigRepo;
    private final MapperConfig mapper;

    public ScheduleConfigService(ScheduleConfigRepo scheduleConfigRepo, MapperConfig mapper) {
        this.scheduleConfigRepo = scheduleConfigRepo;
        this.mapper = mapper;
    }

    public ScheduleConfig updateScheduleConfig(UUID scheduleConfigId, ScheduleConfigDto scheduleConfig) { //TODO: Add link to group and check for privelleges
        Optional<ScheduleConfig> scheduleConfigSource = scheduleConfigRepo.findById(scheduleConfigId);
        if (scheduleConfigSource.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule config not exists");

        mapper.getMapper().map(scheduleConfig, scheduleConfigSource.get());
        scheduleConfigSource.get().setBreaksSchedule(scheduleConfig.breaksSchedule());

        scheduleConfigRepo.save(scheduleConfigSource.get());

        return scheduleConfigSource.get();
    }
}
