package com.example.uchebapi.repos;

import com.example.uchebapi.domain.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SubjectRepo extends CrudRepository<Subject, UUID> {
    long countByGroupId(UUID groupId);
    long deleteByGroupId(UUID groupId);
    List<Subject> findByGroup_Id(UUID id);
}
