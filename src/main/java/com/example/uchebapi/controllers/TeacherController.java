package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.Teacher;
import com.example.uchebapi.dtos.TeacherDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.TeacherRepo;
import com.example.uchebapi.services.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/teacher")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Operation(summary = "Creates teacher")
    @PostMapping("create")
    @AccessControl(fromRepo = TeacherRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Teacher createTeacher(@RequestBody Teacher teacher) {
        return teacherService.createTeacher(teacher);
    }

    @Operation(summary = "Gets teacher")
    @GetMapping("get/{teacherId}")
    @AccessControl(fromRepo = TeacherRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public Teacher getTeacher(@PathVariable UUID teacherId) {
        return teacherService.getTeacher(teacherId);
    }

    @Operation(summary = "Deletes teacher")
    @Transactional
    @DeleteMapping("delete/{teacherId}")
    @AccessControl(fromRepo = TeacherRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Teacher deleteTeacher(@PathVariable UUID teacherId) {
        return teacherService.deleteTeacher(teacherId);
    }

    @Operation(summary = "Updates teacher")
    @PutMapping("update/{teacherId}")
    @AccessControl(fromRepo = TeacherRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Teacher updateTeacher(@PathVariable UUID teacherId,
                                 @RequestBody TeacherDto teacher) {
        return teacherService.updateTeacher(teacherId, teacher);
    }
}
