package com.example.uchebapi.repos;

import com.example.uchebapi.domain.ScheduleConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScheduleConfigRepo extends CrudRepository<ScheduleConfig, UUID> {
    long deleteByGroupId(UUID groupId);
    Optional<ScheduleConfig> findByGroupId(UUID groupId);

}
