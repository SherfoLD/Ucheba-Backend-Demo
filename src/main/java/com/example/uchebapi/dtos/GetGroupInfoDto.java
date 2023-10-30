package com.example.uchebapi.dtos;

import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.domain.ScheduleConfig;
import com.example.uchebapi.enums.GroupRoles;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
public record GetGroupInfoDto(
        String name,
        UUID id,
        List<GroupUser> groupUsers,
        List<UUID> completedTasks
) {
    public List<GroupUserInfo> getGroupUsers() {

        List<GroupUserInfo> groupUsersInfo = new ArrayList<>();
        for (var user:
                groupUsers) {
            groupUsersInfo.add(new GroupUserInfo(user.getUser().getVkId(), user.getRole()));
        }
        return groupUsersInfo;
    }

    public record GroupUserInfo(Integer vkId, GroupRoles role) {
    }
}
