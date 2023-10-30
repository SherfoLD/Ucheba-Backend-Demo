package com.example.uchebapi.controllers;

import com.example.uchebapi.aspects.AccessControl;
import com.example.uchebapi.domain.Subject;
import com.example.uchebapi.dtos.SubjectDto;
import com.example.uchebapi.enums.GroupRoles;
import com.example.uchebapi.repos.SubjectRepo;
import com.example.uchebapi.services.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/subject")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Subject")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Operation(summary = "Creates subject")
    @PostMapping("create")
    @AccessControl(fromRepo = SubjectRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @Operation(summary = "Gets subject")
    @GetMapping("get/{subjectId}")
    @AccessControl(fromRepo = SubjectRepo.class, minimalRequiredRole = GroupRoles.Reader)
    public Subject getSubject(@PathVariable UUID subjectId) {
        return subjectService.getSubject(subjectId);
    }

    @Operation(summary = "Deletes subject")
    @Transactional
    @DeleteMapping("delete/{subjectId}")
    @AccessControl(fromRepo = SubjectRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Subject deleteSubject(@PathVariable UUID subjectId) {
        return subjectService.deleteSubject(subjectId);
    }

    @Operation(summary = "Updates subject")
    @PutMapping("update/{subjectId}")
    @AccessControl(fromRepo = SubjectRepo.class, minimalRequiredRole = GroupRoles.Editor)
    public Subject updateSubject(@PathVariable UUID subjectId,
                                 @RequestBody SubjectDto subject) {
        return subjectService.updateSubject(subjectId, subject);
    }
}