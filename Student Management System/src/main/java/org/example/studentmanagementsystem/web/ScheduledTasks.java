package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.service.ParentService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ParentService parentService;

    public ScheduledTasks(StudentService studentService, TeacherService teacherService, ParentService parentService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.parentService = parentService;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyStudentCheck() {
        List<Student> students = studentService.findAllStudents();
        System.out.println("Daily student check completed. Total students: " + students.size());
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyTeacherCheck() {
        List<Teacher> teachers = teacherService.findAllTeachers();
        System.out.println("Daily teacher check completed. Total teachers: " + teachers.size());
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyParentCheck() {
        List<Parent> parents = parentService.findAllParents();
        System.out.println("Daily teacher check completed. Total teachers: " + parents.size());
    }
}
