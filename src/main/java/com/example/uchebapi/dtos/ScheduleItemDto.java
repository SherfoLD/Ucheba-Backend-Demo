package com.example.uchebapi.dtos;

import com.example.uchebapi.enums.LessonTypes;

import java.util.List;
import java.util.UUID;

public record ScheduleItemDto(UUID subjectId, UUID teacherId, Integer weekDay, Integer lessonOrder, String room, LessonTypes type, Integer week, List<Integer> excludedWeeks) {
}
