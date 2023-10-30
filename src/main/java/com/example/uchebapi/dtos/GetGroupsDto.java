package com.example.uchebapi.dtos;

import com.example.uchebapi.enums.GroupRoles;
import lombok.Builder;

import java.util.UUID;
@Builder
public record GetGroupsDto(UUID id, String name, Integer ownerId, GroupRoles role) {
}
