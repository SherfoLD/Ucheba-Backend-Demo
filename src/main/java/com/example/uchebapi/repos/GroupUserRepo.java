package com.example.uchebapi.repos;

import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.projections.GroupNameInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupUserRepo extends CrudRepository<GroupUser, UUID> {
    long countByGroup_Id(UUID id);
    @Transactional
    @Modifying
    @Query("delete from GroupUser g where g.id = ?1")
    int test(UUID id);
    List<GroupUser> findByGroup_IdInAndRole(Collection<UUID> ids, GroupRoles role);
    GroupUser findByGroup_IdAndRole(UUID id, GroupRoles role);
    Optional<GroupUser> findByGroup_IdAndUser_VkId(UUID id, Integer vkId);

    List<GroupNameInfo> findByUser_VkId(Integer vkId);
}
