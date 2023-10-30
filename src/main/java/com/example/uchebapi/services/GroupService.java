package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.*;
import com.example.uchebapi.dtos.CreateGroupDto;
import com.example.uchebapi.dtos.GetGroupInfoDto;
import com.example.uchebapi.dtos.GroupDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.projections.TeacherInfo;
import com.example.uchebapi.repos.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class GroupService {
    private final VkUserRepo vkUserRepo;

    private final LearningGroupRepo learningGroupRepo;

    private final GroupUserRepo groupUserRepo;
    private final InviteRepo inviteRepo;

    private final SubjectRepo subjectRepo;

    private final ScheduleConfigRepo scheduleConfigRepo;

    private final TeacherRepo teacherRepo;
    private final ScheduleRepo scheduleRepo;

    private final MapperConfig mapper;
    private final ScheduleItemRepo scheduleItemRepo;
    private final TaskRepo taskRepo;

    public GroupService(VkUserRepo vkUserRepo, LearningGroupRepo learningGroupRepo, GroupUserRepo groupUserRepo, InviteRepo inviteRepo, SubjectRepo subjectRepo, ScheduleConfigRepo scheduleConfigRepo, TeacherRepo teacherRepo, ScheduleRepo scheduleRepo, MapperConfig mapper,
                        ScheduleItemRepo scheduleItemRepo,
                        TaskRepo taskRepo) {
        this.vkUserRepo = vkUserRepo;
        this.learningGroupRepo = learningGroupRepo;
        this.groupUserRepo = groupUserRepo;
        this.inviteRepo = inviteRepo;
        this.subjectRepo = subjectRepo;
        this.scheduleConfigRepo = scheduleConfigRepo;
        this.teacherRepo = teacherRepo;
        this.scheduleRepo = scheduleRepo;
        this.mapper = mapper;
        this.scheduleItemRepo = scheduleItemRepo;
        this.taskRepo = taskRepo;
    }

    public LearningGroup create(CreateGroupDto groupDto, Integer vkId) {
        Optional<VkUser> user = vkUserRepo.findByVkId(vkId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");
        }

        if (learningGroupRepo.countByOwnerId(vkId) >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
        };

        LearningGroup group = LearningGroup.builder()
                .name(groupDto.name())
                .ownerId(vkId)
                .build();
        learningGroupRepo.save(group);

        ScheduleConfig scheduleConfig = new ScheduleConfig();
        scheduleConfig.setGroupId(group.getId());
        scheduleConfigRepo.save(scheduleConfig);

        Schedule schedule = new Schedule();
        schedule.setScheduleVersion(1);
        schedule.setGroupId(group.getGroupId());
        scheduleRepo.save(schedule);

        GroupUser groupUser = GroupUser.builder()
                .group(group)
                .user(user.get())
                .role(GroupRoles.Owner)
                .build();
        groupUserRepo.save(groupUser);

        return group;
    }

    @Transactional
    public LearningGroup delete(UUID groupId) {

        Optional<LearningGroup> group = learningGroupRepo.findById(groupId);

        if (group.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group does not exists");

        scheduleItemRepo.deleteByGroupId(group.get().getGroupId());
        scheduleRepo.deleteByGroupId(group.get().getGroupId());
        taskRepo.deleteByGroupId(group.get().getGroupId());
        teacherRepo.deleteByGroupId(group.get().getGroupId());
        subjectRepo.deleteByGroupId(group.get().getGroupId());
        scheduleConfigRepo.deleteByGroupId(group.get().getGroupId());
        learningGroupRepo.delete(group.get());

        return group.get();
    }

    public LearningGroup update(UUID groupId, GroupDto group) {
        Optional<LearningGroup> sourceGroup = learningGroupRepo.findById(groupId);
        if (sourceGroup.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group not exists");

        mapper.getMapper().map(group,sourceGroup.get());

        learningGroupRepo.save(sourceGroup.get());

        return sourceGroup.get();
    }
    public GetGroupInfoDto get(UUID groupId) {
//        GroupInfo groupInfo = groupUserRepo.findByGroup_Id(groupId);
        Optional<LearningGroup> learningGroup = learningGroupRepo.findById(groupId);

        if (learningGroup.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group not exists");



        return GetGroupInfoDto.builder()
                .id(learningGroup.get().getId())
                .name(learningGroup.get().getName())
                .groupUsers(learningGroup.get().getGroupUsers())
                .build();
    }

    public ScheduleConfig getScheduleConfig(UUID groupId) {
        Optional<ScheduleConfig> scheduleConfig = scheduleConfigRepo.findByGroupId(groupId);

        if (scheduleConfig.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group not exists");

        return scheduleConfig.get();
    }

    public GetGroupInfoDto removeUser(UUID groupId, Integer vkId) {
        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(groupId,vkId);
        if (groupUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relation does not exists");
        }

        if (groupUser.get().getRole() == GroupRoles.Owner) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't delete owner");
        }

//        groupUserRepo.delete(groupUser.get().getId());
        groupUserRepo.test(groupUser.get().getId());

//        groupUserRepo.delete(groupUser.get());

        return this.get(groupId);
    }

    public GetGroupInfoDto changeRole(UUID groupId, Integer vkId, GroupRoles role) {
        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(groupId, vkId);
        if (groupUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relation does not exists");
        }

        if (role == GroupRoles.Owner) {
            if (learningGroupRepo.countByOwnerId(vkId) >= 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
            }

            GroupUser ownerRelation = groupUserRepo.findByGroup_IdAndRole(groupId, GroupRoles.Owner);
            ownerRelation.setRole(GroupRoles.Editor);

            LearningGroup group = learningGroupRepo.findById(groupId).get();
            group.setOwnerId(vkId);

            groupUserRepo.save(ownerRelation);
            learningGroupRepo.save(group); // TODO: This is cringe
        }

        groupUser.get().setRole(role);
        groupUserRepo.save(groupUser.get());

        return this.get(groupId);
    }

    public List<Subject> getSubjects(UUID groupId) {
        return subjectRepo.findByGroup_Id(groupId);
    }

    public List<TeacherInfo> getTeachers(UUID groupId) {
        return teacherRepo.findByGroupId(groupId);
//        return teacherRepo.findByGroupId(groupId);
    }
}
