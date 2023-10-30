package com.example.uchebapi.repos;

import com.example.uchebapi.domain.Task;
import com.example.uchebapi.projections.TasksInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TaskRepo extends CrudRepository<Task, UUID> {
    @Transactional
    @Modifying
    @Query("update Task t set t.subjectId = ?1 where t.subjectId = ?2")
    int updateSubjectIdBySubjectId(@Nullable UUID subjectId, UUID subjectId1);
    long countByGroupId(UUID groupId);
    long deleteByGroupId(UUID groupId);
    List<TasksInfo> findByLearningGroup_Id(UUID id);

}
