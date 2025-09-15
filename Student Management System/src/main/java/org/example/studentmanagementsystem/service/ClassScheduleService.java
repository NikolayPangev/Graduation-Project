package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.entities.ClassSchedule;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Teacher;

import java.util.List;
import java.util.Optional;

public interface ClassScheduleService {
    ClassSchedule save(ClassSchedule schedule);
    Optional<ClassSchedule> findById(Long id);
    void delete(ClassSchedule schedule);
    List<ClassSchedule> findByClass(org.example.studentmanagementsystem.model.entities.Class schoolClass);
    List<ClassSchedule> findByTeacher(Teacher teacher);
    Optional<ClassSchedule> findByClassAndSlot(org.example.studentmanagementsystem.model.entities.Class schoolClass, java.time.DayOfWeek day, java.time.LocalTime startTime);
    Optional<ClassSchedule> findByTeacherAndSlot(Teacher teacher, java.time.DayOfWeek day, java.time.LocalTime startTime);
}
