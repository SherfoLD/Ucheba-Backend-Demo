package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.LearningGroup;
import com.example.uchebapi.domain.ScheduleConfig;
import com.example.uchebapi.domain.Subject;
import com.example.uchebapi.dtos.CreateGroupDto;
import com.example.uchebapi.dtos.GetGroupInfoDto;
import com.example.uchebapi.dtos.GroupDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.projections.TeacherInfo;
import com.example.uchebapi.repos.LearningGroupRepo;
import com.example.uchebapi.services.AuthenticationService;
import com.example.uchebapi.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/group")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Group")
public class GroupController {

    private final GroupService groupService;
    private final AuthenticationService authenticationService;

    public GroupController(GroupService groupService, AuthenticationService authenticationService) {
        this.groupService = groupService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Creates group with given name and owner")
    @PostMapping("create")
    public LearningGroup createGroup(@RequestBody CreateGroupDto groupDto) {
        Integer vkId = authenticationService.getVkId();
        return groupService.create(groupDto, vkId);
    }

    @Operation(summary = "Deletes group with given groupId")
    @Transactional
    @DeleteMapping("delete/{groupId}") // TODO: Add check privileges
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Owner)
    public LearningGroup deleteGroup(@PathVariable UUID groupId) {
        return groupService.delete(groupId);
    }

    @Operation(summary = "Updates name of group")
    @PutMapping("update/{groupId}") // TODO: Add check privileges
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Owner)
    public LearningGroup updateGroup(@PathVariable UUID groupId,
                                     @RequestBody GroupDto group) {
        return groupService.update(groupId, group);
    }

    @Operation(summary = "Gets information of particular group")
    @GetMapping("get/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public GetGroupInfoDto getGroup(@PathVariable UUID groupId) {
        return groupService.get(groupId);
    }

    @Operation(summary = "Gets schedule config of particular group")
    @GetMapping("getScheduleConfig/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public ScheduleConfig getScheduleConfig(@PathVariable UUID groupId) {
        return groupService.getScheduleConfig(groupId);
    }

    @Operation(summary = "Changes the role of some user. There can be only one group owner")
    @PutMapping("changeRole/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Owner)
    public GetGroupInfoDto changeRole(@PathVariable UUID groupId,
                                      @RequestParam Integer vkId,
                                      @RequestParam GroupRoles role) {
        if (authenticationService.getVkId().equals(vkId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cant change role on yourself");

        return groupService.changeRole(groupId, vkId, role);
    }

    @Operation(summary = "Deletes user from group (can be used to leave from group)")
    @Transactional
    @DeleteMapping("removeUser/{groupId}")
    public GetGroupInfoDto removeUser(@PathVariable UUID groupId,
                                      @RequestParam Integer vkId) {
        if (authenticationService.getPrivilegeLevel(groupId) > GroupRoles.Owner.ordinal() &&
                !authenticationService.getVkId().equals(vkId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can delete from group");
        return groupService.removeUser(groupId, vkId);
    }

    @Operation(summary = "Gets subjects from group")
    @GetMapping("getSubjects/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public List<Subject> getSubjects(@PathVariable UUID groupId) {
        return groupService.getSubjects(groupId);
    }

    @Operation(summary = "Gets teachers from group")
    @GetMapping("getTeachers/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public List<TeacherInfo> getTeachers(@PathVariable UUID groupId) {
        return groupService.getTeachers(groupId);
    }
}
