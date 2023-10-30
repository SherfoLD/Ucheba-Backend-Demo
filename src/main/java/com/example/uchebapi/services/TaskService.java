package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.Task;
import com.example.uchebapi.dtos.TaskDto;
import com.example.uchebapi.repos.TaskRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final MapperConfig mapper;

    public TaskService(TaskRepo taskRepo, MapperConfig mapper) {
        this.taskRepo = taskRepo;
        this.mapper = mapper;
    }

    public Task createTask(Task task) {
        if (taskRepo.countByGroupId(task.getGroupId()) >= 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
        }

        return taskRepo.save(task);
    }

    public Task deleteTask(UUID taskId) {
        Optional<Task> task = taskRepo.findById(taskId);
        if (task.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task not exists");

        taskRepo.delete(task.get());
        return task.get();
    }

    public Task updateTask(UUID taskId, TaskDto task) {
        Optional<Task> taskSource = taskRepo.findById(taskId);
        if (taskSource.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task not exists");

        mapper.getMapper().map(task, taskSource.get());
        taskSource.get().setAttachments(task.attachments());

        if (task.attachments().size() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");
        }

        if (task.attachments() == null) {
            taskSource.get().setAttachments(null);
        }
        if (task.subjectId() == null) {
            taskSource.get().setSubjectId(null);
        }
        if (task.deadline() == null) {
            taskSource.get().setDeadline(null);
        }
        taskRepo.save(taskSource.get());

        return taskSource.get();
    }
}
