package com.example.uchebapi.repos;

import com.example.uchebapi.domain.ScheduleItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ScheduleItemRepo extends CrudRepository<ScheduleItem, UUID> {
    long countByGroupIdAndWeekDayAndWeek(UUID groupId, Integer weekDay, Integer week);
    long countByGroupId(UUID groupId);
    long deleteBySubjectId(UUID subjectId);
    long deleteByGroupId(UUID groupId);
    @Transactional
    @Modifying
    @Query("update ScheduleItem s set s.lessonOrder = ?1 where s.id = ?2")
    void updateLessonOrderById(Integer lessonOrder, UUID id);
    @Transactional
    @Modifying
    @Query("update ScheduleItem s set s.teacherId = ?1 where s.teacherId = ?2")
    void updateTeacherIdByTeacherId(UUID teacherId, UUID teacherId1);
    List<ScheduleItem> findByGroupId(UUID groupId);
}
