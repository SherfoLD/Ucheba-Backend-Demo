package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.dtos.*;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.projections.GroupNameInfo;
import com.example.uchebapi.repos.LearningGroupRepo;
import com.example.uchebapi.repos.TaskRepo;
import com.example.uchebapi.security.VkFriendsAreFriendsResolver;
import com.example.uchebapi.services.AuthenticationService;
import com.example.uchebapi.services.InviteService;
import com.example.uchebapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/user")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    private final InviteService inviteService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(UserService userService, InviteService inviteService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.inviteService = inviteService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Invites user to group. Invite must be unique")
    @PutMapping("inviteUser/{groupId}") // TODO: check group is exists
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public InviteDto inviteUser(@PathVariable UUID groupId,
                                @RequestParam(required = false) Integer vkId,
                                @RequestHeader(required = false) String sign) {
        if (vkId != null && !VkFriendsAreFriendsResolver.isSignValid(authenticationService.getVkId(), vkId, sign))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Friends are friends sign is not valid");

        return inviteService.inviteUser(groupId, vkId);
    }

    @Operation(summary = "Accepts invite to group")
    @PutMapping("acceptInvite/{inviteId}")
    public InviteDto acceptInvite(@PathVariable UUID inviteId) {
        Integer vkId = authenticationService.getVkId();
        return inviteService.acceptInvite(inviteId, vkId);
    }

    @Operation(summary = "Rejects invite to group")
    @PutMapping("rejectInvite/{inviteId}")
    public ResponseEntity<String> rejectInvite(@PathVariable UUID inviteId) {
        Integer vkId = authenticationService.getVkId();
        return inviteService.rejectInvite(inviteId, vkId);
    }

    @Operation(summary = "Gets information of particular user")
    @GetMapping("getGroups")
    public List<GetGroupsDto> getUserGroups() {
        Integer vkId = authenticationService.getVkId();
        return userService.getGroups(vkId);
    }

    @Operation(summary = "Gets information of invites for particular user")
    @GetMapping("getInvites")
    public List<GetInvitesDto> getInvites() {
        Integer vkId = authenticationService.getVkId();
        return inviteService.getInvites(vkId);
    }

    @Operation(summary = "Gets tasks and its status of particular group user")
    @GetMapping("getTasks/{groupId}")
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public List<TaskShortDto> getUserGroups(@PathVariable UUID groupId) {
        Integer vkId = authenticationService.getVkId();
        return userService.getTasks(groupId, vkId);
    }

    @Operation(summary = "Reverses status of task")
    @GetMapping("setTask/{groupId}/{taskId}") // TODO: Add check privileges
    @AccessControl(fromRepo = LearningGroupRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public void getUserGroups(@PathVariable UUID groupId,
                              @PathVariable UUID taskId) {
        Integer vkId = authenticationService.getVkId();
        userService.setTask(groupId, taskId, vkId);
    }

    @Operation(summary = "Gets task")
    @GetMapping("get/{taskId}")
    @AccessControl(fromRepo = TaskRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public TaskDto getTask(@PathVariable UUID taskId) {
        Integer vkId = authenticationService.getVkId();
        return userService.getTask(taskId, vkId);
    }
}
