package com.example.uchebapi.projections;

import com.example.uchebapi.enums.GroupRoles;

import java.util.UUID;

/**
 * Projection for {@link com.example.uchebapi.domain.GroupUser}
 */
public interface GroupNameInfo {
    LearningGroupInfo getGroup();

    GroupRoles getRole();

    /**
     * Projection for {@link com.example.uchebapi.domain.LearningGroup}
     */
    interface LearningGroupInfo {
        UUID getId();
        String getName();
        Integer getOwnerId();
    }
}