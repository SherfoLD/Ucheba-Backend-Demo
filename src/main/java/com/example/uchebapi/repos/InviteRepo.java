package com.example.uchebapi.repos;

import com.example.uchebapi.domain.Invite;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepo extends CrudRepository<Invite, UUID> {
    long countByVkId(Integer vkId);
    long countByGroupId(UUID groupId);
    Optional<Invite> findByIdAndVkId(UUID id, Integer vkId);
    List<Invite> findByVkId(Integer vkId);
    Optional<Invite> findByGroupIdAndVkId(UUID groupId, Integer vkId);
}
