package com.example.uchebapi.services;

import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.domain.Invite;
import com.example.uchebapi.domain.LearningGroup;
import com.example.uchebapi.domain.VkUser;
import com.example.uchebapi.dtos.GetInvitesDto;
import com.example.uchebapi.dtos.InviteDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.GroupUserRepo;
import com.example.uchebapi.repos.InviteRepo;
import com.example.uchebapi.repos.LearningGroupRepo;
import com.example.uchebapi.repos.VkUserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class InviteService {
    private final InviteRepo inviteRepo;
    private final GroupUserRepo groupUserRepo;
    private final LearningGroupRepo learningGroupRepo;
    private final VkUserRepo vkUserRepo;

    public InviteService(InviteRepo inviteRepo, GroupUserRepo groupUserRepo, LearningGroupRepo learningGroupRepo, VkUserRepo vkUserRepo) {
        this.inviteRepo = inviteRepo;
        this.groupUserRepo = groupUserRepo;
        this.learningGroupRepo = learningGroupRepo;
        this.vkUserRepo = vkUserRepo;
    }

    public InviteDto inviteUser(UUID groupId, Integer vkId) {
        Optional<Invite> invite = inviteRepo.findByGroupIdAndVkId(groupId, vkId);

        if (inviteRepo.countByGroupId(groupId) >= 50 || groupUserRepo.countByGroup_Id(groupId) >= 150) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
        }

        if (vkId != null && inviteRepo.countByVkId(vkId) >= 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
        }

        if (invite.isPresent() && invite.get().getValidUntil().compareTo(new Date()) > 0)
            return new InviteDto(invite.get().getId());

        if (groupUserRepo.findByGroup_IdAndUser_VkId(groupId, vkId).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already in group");

        if (vkId == null && invite.isPresent())
            return new InviteDto(invite.get().getId());


        Invite createdInvite = inviteRepo.save(Invite.builder()
                .groupId(groupId)
                .vkId(vkId)
                .validUntil(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)))
                .validCount(vkId != null ? 1 : 20)
                .build());

        return new InviteDto(createdInvite.getId());
    }

    public InviteDto acceptInvite(UUID inviteId, Integer vkId) {
        Optional<Invite> invite = inviteRepo.findById(inviteId);
        if (invite.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite not exists");

        if (invite.get().getValidUntil().compareTo(new Date()) < 0 || invite.get().getValidCount() == 0) {
            inviteRepo.delete(invite.get());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite is expired");
        }

        Optional<LearningGroup> learningGroup = learningGroupRepo.findById(invite.get().getGroupId());
        if (learningGroup.isEmpty()) {
            inviteRepo.delete(invite.get());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group does not exists");
        }

        Optional<VkUser> vkUser = vkUserRepo.findByVkId(vkId);

        if (vkUser.isEmpty()) {
            VkUser newVkUser = VkUser.builder().vkId(vkId).build();
            vkUserRepo.save(newVkUser);
            vkUser = Optional.of(newVkUser);
        }

        if (groupUserRepo.findByGroup_IdAndUser_VkId(learningGroup.get().getGroupId(), vkId).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already in group");

        groupUserRepo.save(GroupUser.builder()
                .group(learningGroup.get())
                .user(vkUser.get())
                .role(GroupRoles.Reader)
                .build());

        if (invite.get().getValidCount() - 1 == 0) {
            inviteRepo.delete(invite.get());
        } else {
            invite.get().setValidCount(invite.get().getValidCount() - 1);
            inviteRepo.save(invite.get());
        }

        return new InviteDto(invite.get().getGroupId());
    }

    public ResponseEntity<String> rejectInvite(UUID inviteId, Integer vkId) {
        Optional<Invite> invite = inviteRepo.findByIdAndVkId(inviteId, vkId);
        if (invite.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite not exists");
        if (!invite.get().getVkId().equals(vkId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cant delete invite");
        }
        if (invite.get().getValidUntil().compareTo(new Date()) < 0 || invite.get().getValidCount() == 0) {
            inviteRepo.delete(invite.get());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite is expired");
        }
        Optional<LearningGroup> learningGroup = learningGroupRepo.findById(invite.get().getGroupId());
        if (learningGroup.isEmpty()) {
            inviteRepo.delete(invite.get());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group does not exists");
        }
        inviteRepo.delete(invite.get());

        return new ResponseEntity<>(
                "Invite has been rejected",
                HttpStatus.OK);
    }

    public List<GetInvitesDto> getInvites(Integer vkId) {
        List<Invite> invites = inviteRepo.findByVkId(vkId);

        List<GetInvitesDto> invitesDtos = new ArrayList<>();
        for (var invite : invites) {
            if (invite.getValidUntil().compareTo(new Date()) < 0 || invite.getValidCount() == 0) {
                inviteRepo.delete(invite);
            }
            Optional<LearningGroup> learningGroup = learningGroupRepo.findById(invite.getGroupId());
            if (learningGroup.isEmpty()) {
                inviteRepo.delete(invite);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group does not exists");
            }
            invitesDtos.add(GetInvitesDto.builder()
                    .inviteId(invite.getId())
                    .ownerId(learningGroup.get().getOwnerId())
                    .name(learningGroup.get().getName())
                    .build());
        }

        return invitesDtos;
    }
}
