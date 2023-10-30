package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.Subject;
import com.example.uchebapi.dtos.SubjectDto;
import com.example.uchebapi.repos.ScheduleItemRepo;
import com.example.uchebapi.repos.SubjectRepo;
import com.example.uchebapi.repos.TaskRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class SubjectService {

    private final SubjectRepo subjectRepo;
    private final ScheduleItemRepo scheduleItemRepo;
    private final MapperConfig mapper;
    private final TaskRepo taskRepo;

    public SubjectService(SubjectRepo subjectRepo, ScheduleItemRepo scheduleItemRepo, MapperConfig mapper, TaskRepo taskRepo) {
        this.subjectRepo = subjectRepo;
        this.scheduleItemRepo = scheduleItemRepo;
        this.mapper = mapper;
        this.taskRepo = taskRepo;
    }

    public Subject createSubject(Subject subject) {
        if (subjectRepo.countByGroupId(subject.getGroupId()) >= 30)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");

        return subjectRepo.save(subject);
    }

    public Subject getSubject(UUID subjectId) {
        Optional<Subject> subject = subjectRepo.findById(subjectId);
        if (subject.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject not exists");

        return subject.get();
    }

    public Subject deleteSubject(UUID subjectId) {
        Optional<Subject> subject = subjectRepo.findById(subjectId);
        if (subject.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject not exists");

        scheduleItemRepo.deleteBySubjectId(subjectId);
        taskRepo.updateSubjectIdBySubjectId(null, subjectId);
        subjectRepo.delete(subject.get());
        return subject.get();
    }

    public Subject updateSubject(UUID subjectId, SubjectDto subject) {
        Optional<Subject> subjectSource = subjectRepo.findById(subjectId);
        if (subjectSource.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject not exists");

        mapper.getMapper().map(subject, subjectSource.get());
        subjectRepo.save(subjectSource.get());

        return subjectSource.get();
    }
}
