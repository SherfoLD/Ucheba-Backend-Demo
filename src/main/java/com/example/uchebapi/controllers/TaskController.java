package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.Task;
import com.example.uchebapi.dtos.TaskDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.TaskRepo;
import com.example.uchebapi.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/task")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Creates task")
    @PostMapping("create")
    @AccessControl(fromRepo = TaskRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @Operation(summary = "Deletes task")
    @Transactional
    @DeleteMapping("delete/{taskId}")
    @AccessControl(fromRepo = TaskRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Task deleteTask(@PathVariable UUID taskId) {
        return taskService.deleteTask(taskId);
    }

    @Operation(summary = "Updates task")
    @PutMapping("update/{taskId}")
    @AccessControl(fromRepo = TaskRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Task updateTask(@PathVariable UUID taskId,
                           @RequestBody TaskDto task) {
        return taskService.updateTask(taskId,task);
    }
}
