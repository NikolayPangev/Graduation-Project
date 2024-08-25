package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.service.GradeService;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.TeacherService;
import org.example.studentmanagementsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Mock
    private StudentService studentService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private GradeService gradeService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;

    @InjectMocks
    private StudentController studentController;

    private Student student;
    private Teacher teacher;
    private Subject subject;
    private Grade grade;
    private Class studentClass;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        studentClass = new Class();
        studentClass.setClassId(1L);

        student = new Student();
        student.setUsername("studentUser");
        student.setClasses(studentClass);

        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setSubject(new Subject());
        studentClass.setTeachers(Set.of(teacher));

        subject = new Subject();
        subject.setSubjectId(1L);
        subject.setTeacher(Collections.singletonList(teacher));

        grade = new Grade();
        grade.setGrade(90);
    }

    @Test
    void getDashboard() {
        String viewName = studentController.getDashboard();
        assertEquals("student/student_dashboard", viewName);
    }

    @Test
    void viewClass() {
        when(principal.getName()).thenReturn("studentUser");
        when(studentService.findByUsername("studentUser")).thenReturn(Optional.of(student));
        when(studentService.findAllByClassId(1L)).thenReturn(Collections.singletonList(student));

        String viewName = studentController.viewClass(model, principal);

        verify(model, times(1)).addAttribute(eq("students"), any(List.class));
        assertEquals("student/view_class", viewName);
    }

    @Test
    void viewTeachers() {
        when(principal.getName()).thenReturn("studentUser");
        when(studentService.findByUsername("studentUser")).thenReturn(Optional.of(student));
        when(teacherService.findByClassId(1L)).thenReturn(Collections.singletonList(teacher));

        String viewName = studentController.viewTeachers(model, principal);

        verify(model, times(1)).addAttribute(eq("teachers"), any(List.class));
        assertEquals("student/view_teachers", viewName);
    }

    @Test
    void viewGrades() {
        when(principal.getName()).thenReturn("studentUser");
        when(studentService.findByUsername("studentUser")).thenReturn(Optional.of(student));
        when(gradeService.findByStudentAndSubject(any(Student.class), any(Subject.class))).thenReturn(Collections.singletonList(grade));

        String viewName = studentController.viewGrades(model, principal);

        verify(model, times(1)).addAttribute(eq("subjectWithGradesList"), any(List.class));
        assertEquals("student/view_grades", viewName);
    }

    @Test
    void viewProfile() {
        when(principal.getName()).thenReturn("studentUser");
        when(studentService.findByUsername("studentUser")).thenReturn(Optional.of(student));

        String viewName = studentController.viewProfile(model, principal);

        verify(model, times(1)).addAttribute("user", student);
        assertEquals("student/view_profile", viewName);
    }

    @Test
    void viewProfile_studentNotFound() {
        when(principal.getName()).thenReturn("studentUser");
        when(studentService.findByUsername("studentUser")).thenReturn(Optional.empty());

        String viewName = studentController.viewProfile(model, principal);

        assertEquals("error", viewName);
    }

    @Test
    void logoutConfirmation() {
        String viewName = studentController.logoutConfirmation();
        assertEquals("student/logout_confirmation", viewName);
    }
}
