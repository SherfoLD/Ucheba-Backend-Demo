package com.example.uchebapi.dtos;

import com.example.uchebapi.enums.GroupRoles;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetInvitesDto(UUID inviteId, String name, Integer ownerId) {
}
