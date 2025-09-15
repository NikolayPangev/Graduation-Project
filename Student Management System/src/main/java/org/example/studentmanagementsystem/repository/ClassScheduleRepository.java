package org.example.studentmanagementsystem.repository;

import org.example.studentmanagementsystem.model.entities.ClassSchedule;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    // class timetable
    List<ClassSchedule> findBySchoolClassOrderByDayOfWeekAscStartTimeAsc(Class schoolClass);

    // teacher timetable
    List<ClassSchedule> findByTeacherOrderByDayOfWeekAscStartTimeAsc(Teacher teacher);

    // convenience: find a single slot for a class
    Optional<ClassSchedule> findBySchoolClassAndDayOfWeekAndStartTime(Class schoolClass, DayOfWeek dayOfWeek, LocalTime startTime);

    // convenience: find a single slot for a teacher
    Optional<ClassSchedule> findByTeacherAndDayOfWeekAndStartTime(Teacher teacher, DayOfWeek dayOfWeek, LocalTime startTime);
}
