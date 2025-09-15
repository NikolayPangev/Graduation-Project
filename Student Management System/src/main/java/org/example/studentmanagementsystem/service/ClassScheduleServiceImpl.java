package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.ClassSchedule;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.repository.ClassScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {

    private final ClassScheduleRepository repository;

    public ClassScheduleServiceImpl(ClassScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClassSchedule save(ClassSchedule schedule) {
        return repository.save(schedule);
    }

    @Override
    public Optional<ClassSchedule> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(ClassSchedule schedule) {
        repository.delete(schedule);
    }

    @Override
    public List<ClassSchedule> findByClass(Class schoolClass) {
        return repository.findBySchoolClassOrderByDayOfWeekAscStartTimeAsc(schoolClass);
    }

    @Override
    public List<ClassSchedule> findByTeacher(Teacher teacher) {
        return repository.findByTeacherOrderByDayOfWeekAscStartTimeAsc(teacher);
    }

    @Override
    public Optional<ClassSchedule> findByClassAndSlot(Class schoolClass, DayOfWeek day, LocalTime startTime) {
        return repository.findBySchoolClassAndDayOfWeekAndStartTime(schoolClass, day, startTime);
    }

    @Override
    public Optional<ClassSchedule> findByTeacherAndSlot(Teacher teacher, DayOfWeek day, LocalTime startTime) {
        return repository.findByTeacherAndDayOfWeekAndStartTime(teacher, day, startTime);
    }
}
