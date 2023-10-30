package com.example.uchebapi.repos;

import com.example.uchebapi.domain.Schedule;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ScheduleRepo extends CrudRepository<Schedule, UUID> {
    long deleteByGroupId(UUID groupId);
    Schedule findByGroupId(UUID groupId);
}
