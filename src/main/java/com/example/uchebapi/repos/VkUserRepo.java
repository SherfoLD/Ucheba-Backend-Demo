package com.example.uchebapi.repos;

import com.example.uchebapi.domain.VkUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface VkUserRepo extends CrudRepository<VkUser, UUID> {
    Optional<VkUser> findByVkId(Integer vkId);
}