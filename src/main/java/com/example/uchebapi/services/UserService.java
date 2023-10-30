package com.example.uchebapi.services;


import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.domain.Subject;
import com.example.uchebapi.domain.Task;
import com.example.uchebapi.domain.VkUser;
import com.example.uchebapi.dtos.AuthDto;
import com.example.uchebapi.dtos.GetGroupsDto;
import com.example.uchebapi.dtos.TaskDto;
import com.example.uchebapi.dtos.TaskShortDto;
import com.example.uchebapi.projections.GroupNameInfo;
import com.example.uchebapi.projections.TasksInfo;
import com.example.uchebapi.repos.GroupUserRepo;
import com.example.uchebapi.repos.SubjectRepo;
import com.example.uchebapi.repos.TaskRepo;
import com.example.uchebapi.repos.VkUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final VkUserRepo vkUserRepo;
    private final GroupUserRepo groupUserRepo;
    private final SubjectRepo subjectRepo;
    private final TaskRepo taskRepo;

    @Autowired
    public UserService(VkUserRepo vkUserRepo, GroupUserRepo groupUserRepo, SubjectRepo subjectRepo, TaskRepo taskRepo) {
        this.vkUserRepo = vkUserRepo;
        this.groupUserRepo = groupUserRepo;
        this.subjectRepo = subjectRepo;
        this.taskRepo = taskRepo;
    }

    public AuthDto login(Integer vkId, String accessToken) {
        Optional<VkUser> user = vkUserRepo.findByVkId(vkId);

        if (user.isEmpty()) {
            user = Optional.of(
                    VkUser.builder()
                            .vkId(vkId)
                            .build());
            vkUserRepo.save(user.get());
        } // TODO: FOR TESTING
//        if (user.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vk id does not exists in db");
//        }
//        if (user.get().getGroups().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not have any group");
//        }


        return AuthDto.builder()
                .vkId(user.get().getVkId())
                .accessToken(accessToken)
                .build();
    }

    public List<GetGroupsDto> getGroups(Integer vkId) {
        List<GroupNameInfo> groupUsers = groupUserRepo.findByUser_VkId(vkId);
        List<GetGroupsDto> groupInfoList = new ArrayList<>();
        for (var group :
                groupUsers) {
            groupInfoList.add(GetGroupsDto.builder()
                    .id(group.getGroup().getId())
                    .name(group.getGroup().getName())
                    .ownerId(group.getGroup().getOwnerId())
                    .role(group.getRole())
                    .build());
        }
        return groupInfoList;
    }

    public List<TaskShortDto> getTasks(UUID groupId, Integer vkId) {
        List<TasksInfo> tasksInfo = taskRepo.findByLearningGroup_Id(groupId);
        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(groupId, vkId);
        if (groupUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relation not exists");

        List<TaskShortDto> tasks = new ArrayList<>();
        for (TasksInfo task : tasksInfo) {
            tasks.add(
                    TaskShortDto.builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .deadline(task.getDeadline())
                            .subject(task.getSubject() == null ? null : task.getSubject().getName())
                            .isDone(groupUser.get().checkCompleted(task.getId()))
                            .build()
            );
        }
        return tasks;
    }

    public void setTask(UUID groupId, UUID taskId, Integer vkId) {
        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(groupId, vkId);
        if (groupUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Relation not exists");

        groupUser.get().setCompleted(taskId);
        groupUserRepo.save(groupUser.get());
    }

    public TaskDto getTask(UUID taskId, Integer vkId) {
        Optional<Task> task = taskRepo.findById(taskId);
        if (task.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task not exists");

        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(task.get().getGroupId(), vkId);

        Optional<Subject> subject = task.get().getSubjectId() != null ? subjectRepo.findById(task.get().getSubjectId())
                : Optional.empty();

        return TaskDto.builder()
                .id(task.get().getId())
                .subjectId(task.get().getSubjectId())
                .title(task.get().getTitle())
                .deadline(task.get().getDeadline())
                .content(task.get().getContent())
                .attachments(task.get().getAttachments())
                .isDone(groupUser.map(user -> user.checkCompleted(taskId)).orElse(false))
                .subjectName(subject.isEmpty() ? "" : subject.get().getName())
                .build();
    }
}