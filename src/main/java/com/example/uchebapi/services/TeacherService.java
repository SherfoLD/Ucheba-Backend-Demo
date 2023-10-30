package com.example.uchebapi.services;

import com.example.uchebapi.configs.MapperConfig;
import com.example.uchebapi.domain.Teacher;
import com.example.uchebapi.dtos.TeacherDto;
import com.example.uchebapi.repos.ScheduleItemRepo;
import com.example.uchebapi.repos.TeacherRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepo teacherRepo;
    private final MapperConfig mapper;
    private final ScheduleItemRepo scheduleItemRepo;

    public TeacherService(TeacherRepo teacherRepo, MapperConfig mapper, ScheduleItemRepo scheduleItemRepo) {
        this.teacherRepo = teacherRepo;
        this.mapper = mapper;
        this.scheduleItemRepo = scheduleItemRepo;
    }

    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepo.countByGroupId(teacher.getGroupId()) >= 30)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AMOUNT_ERROR");

        return teacherRepo.save(teacher);
    }

    public Teacher getTeacher(UUID teacherId) {
        Optional<Teacher> teacher = teacherRepo.findById(teacherId);
        if (teacher.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not exists");

        return teacher.get();
    }

    public Teacher deleteTeacher(UUID teacherId) {
        Optional<Teacher> teacher = teacherRepo.findById(teacherId);
        if (teacher.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not exists");

        scheduleItemRepo.updateTeacherIdByTeacherId(null, teacherId);
        teacherRepo.delete(teacher.get());
        return teacher.get();
    }

    public Teacher updateTeacher(UUID teacherId, TeacherDto teacher) {
        Optional<Teacher> teacherSource = teacherRepo.findById(teacherId);
        if (teacherSource.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher not exists");

        mapper.getMapper().map(teacher, teacherSource.get());

        teacherRepo.save(teacherSource.get());

        return teacherSource.get();
    }
}
