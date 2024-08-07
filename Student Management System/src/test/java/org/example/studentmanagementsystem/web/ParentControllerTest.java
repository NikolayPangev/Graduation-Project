package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.StudentDTO;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.service.GradeService;
import org.example.studentmanagementsystem.service.ParentService;
import org.example.studentmanagementsystem.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParentControllerTest {

    @Mock
    private ParentService parentService;
    @Mock
    private StudentService studentService;
    @Mock
    private GradeService gradeService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @InjectMocks
    private ParentController parentController;

    private Parent parent;
    private Student student;
    private Teacher teacher;
    private Subject subject;
    private Grade grade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        parent = new Parent();
        parent.setUsername("parentUser");
        parent.setChildren(new ArrayList<>());

        student = new Student();
        student.setUserId(1L);
        student.setClasses(new Class());
        parent.getChildren().add(student);

        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setSubject(new Subject());
        student.getClasses().setTeachers(Collections.singletonList(teacher));

        subject = new Subject();
        subject.setSubjectId(1L);
        subject.setTeacher(Collections.singletonList(teacher));

        grade = new Grade();
        grade.setGrade(90);
    }

    @Test
    void dashboard() {
        when(principal.getName()).thenReturn("parentUser");
        when(parentService.findByUsername("parentUser")).thenReturn(Optional.of(parent));

        String viewName = parentController.dashboard(principal, model);

        verify(model, times(1)).addAttribute("parent", parent);
        assertEquals("parent/parent_dashboard", viewName);
    }

    @Test
    void viewChildren() {
        when(principal.getName()).thenReturn("parentUser");
        when(parentService.findByUsername("parentUser")).thenReturn(Optional.of(parent));

        String viewName = parentController.viewChildren(model, principal);

        verify(model, times(1)).addAttribute(eq("children"), any(List.class));
        assertEquals("parent/view_children", viewName);
    }

    @Test
    void childDashboard() {
        when(studentService.findById(1L)).thenReturn(Optional.of(student));

        String viewName = parentController.childDashboard(1L, model);

        verify(model, times(1)).addAttribute(eq("child"), any(StudentDTO.class));
        assertEquals("parent/child_dashboard", viewName);
    }

    @Test
    void childGrades() {
        when(studentService.findById(1L)).thenReturn(Optional.of(student));
        when(gradeService.findByStudentAndSubject(any(Student.class), any(Subject.class))).thenReturn(Collections.singletonList(grade));

        String viewName = parentController.childGrades(1L, model);

        verify(model, times(1)).addAttribute(eq("subjectWithGradesList"), any(List.class));
        assertEquals("parent/view_grades", viewName);
    }

    @Test
    void childTeachers() {
        when(studentService.findById(1L)).thenReturn(Optional.of(student));

        String viewName = parentController.childTeachers(1L, model);

        verify(model, times(1)).addAttribute("teachers", student.getClasses().getTeachers());
        verify(model, times(1)).addAttribute("child", student);
        assertEquals("parent/view_teachers", viewName);
    }

    @Test
    void viewParentProfile() {
        when(principal.getName()).thenReturn("parentUser");
        when(parentService.findByUsername("parentUser")).thenReturn(Optional.of(parent));

        String viewName = parentController.viewParentProfile(model, principal);

        verify(model, times(1)).addAttribute("user", parent);
        assertEquals("parent/view_profile", viewName);
    }

    @Test
    void parentLogoutConfirmation() {
        String viewName = parentController.parentLogoutConfirmation();
        assertEquals("parent/logout_confirmation", viewName);
    }
}