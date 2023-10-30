package com.example.uchebapi.repos;

import com.example.uchebapi.domain.Teacher;
import com.example.uchebapi.projections.TeacherInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TeacherRepo  extends CrudRepository<Teacher, UUID> {
    long countByGroupId(UUID groupId);
    long deleteByGroupId(UUID groupId);
    List<TeacherInfo> findByGroupId(UUID groupId);
    List<Teacher> findByGroup_Id(UUID id);
}
