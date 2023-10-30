package com.example.uchebapi.repos;

import com.example.uchebapi.domain.LearningGroup;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface LearningGroupRepo extends CrudRepository<LearningGroup, UUID> {
    long countByOwnerId(Integer ownerId);
}
