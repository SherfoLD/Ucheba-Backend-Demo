package com.example.uchebapi.dtos;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record ScheduleConfigDto(LocalDate firstDay, Integer startTime, Integer weeksCycle, Integer lessonDuration, List<Integer> breaksSchedule) {
}
