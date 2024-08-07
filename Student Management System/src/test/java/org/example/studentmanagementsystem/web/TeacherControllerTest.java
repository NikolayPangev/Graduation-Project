package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.GradeForm;
import org.example.studentmanagementsystem.model.entities.*;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @Mock
    private ClassService classService;
    @Mock
    private StudentService studentService;
    @Mock
    private ParentService parentService;
    @Mock
    private GradeService gradeService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private UserService userService;
    @Mock
    private Model model;
    @Mock
    private Principal principal;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private Student student;
    private Class studentClass;
    private Subject subject;
    private Grade grade;
    private Parent parent;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        teacher = new Teacher();
        teacher.setUserId(1L);
        teacher.setUsername("teacherUser");

        studentClass = new Class();
        studentClass.setClassId(1L);
        studentClass.setStudents(new HashSet<>(Collections.singletonList(student)));

        student = new Student();
        student.setClasses(studentClass);
        student.setUsername("studentUser");

        subject = new Subject();
        subject.setSubjectId(1L);
        subject.setTeacher(Collections.singletonList(teacher));

        grade = new Grade();
        grade.setGrade(90);
        grade.setStudent(student);
        grade.setSubject(subject);

        parent = new Parent();
        parent.setUsername("parentUser");

        user = new User();
        user.setUsername("teacherUser");
    }

    @Test
    void viewDashboard() {
        String viewName = teacherController.viewDashboard();
        assertEquals("teacher/teacher_dashboard", viewName);
    }

    @Test
    void viewClasses() {
        when(authentication.getName()).thenReturn("teacherUser");
        when(userService.findByUsername("teacherUser")).thenReturn(Optional.of(teacher));
        when(classService.findClassesByTeacher(teacher)).thenReturn(Collections.singletonList(studentClass));

        String viewName = teacherController.viewClasses(model);

        verify(model, times(1)).addAttribute(eq("classes"), any(List.class));
        assertEquals("teacher/view_classes", viewName);
    }

    @Test
    void viewClass() {
        when(classService.findById(1L)).thenReturn(Optional.of(studentClass));

        String viewName = teacherController.viewClass(1L, model);

        verify(model, times(1)).addAttribute(eq("students"), any(List.class));
        verify(model, times(1)).addAttribute(eq("class"), eq(studentClass));
        assertEquals("teacher/view_class", viewName);
    }

    @Test
    void viewClass_classNotFound() {
        when(classService.findById(1L)).thenReturn(Optional.empty());

        String viewName = teacherController.viewClass(1L, model);

        verify(model, times(1)).addAttribute("errorMessage", "Class not found");
        assertEquals("error", viewName);
    }

    @Test
    void viewStudents() {
        when(authentication.getName()).thenReturn("teacherUser");
        when(userService.findByUsername("teacherUser")).thenReturn(Optional.of(teacher));
        when(studentService.findStudentsByTeacherOrdered(teacher)).thenReturn(Collections.singletonList(student));

        String viewName = teacherController.viewStudents(model);

        verify(model, times(1)).addAttribute(eq("students"), any(List.class));
        assertEquals("teacher/view_students", viewName);
    }

    @Test
    void viewParents() {
        when(authentication.getName()).thenReturn("teacherUser");
        when(userService.findByUsername("teacherUser")).thenReturn(Optional.of(teacher));
        when(classService.findClassesByTeacher(teacher)).thenReturn(Collections.singletonList(studentClass));
        when(studentService.findStudentsByClass(studentClass)).thenReturn(Collections.singletonList(student));
        when(parentService.findParentsByStudent(student)).thenReturn(Collections.singletonList(parent));

        String viewName = teacherController.viewParents(model);

        verify(model, times(1)).addAttribute(eq("parents"), any(List.class));
        assertEquals("teacher/view_parents", viewName);
    }

    @Test
    void viewTeachers() {
        when(teacherService.findAllTeachers()).thenReturn(Collections.singletonList(teacher));

        String viewName = teacherController.viewTeachers(model);

        verify(model, times(1)).addAttribute(eq("teachers"), any(List.class));
        assertEquals("teacher/view_teachers", viewName);
    }

    @Test
    void showAddGradeForm() {
        when(studentService.findById(1L)).thenReturn(Optional.of(student));
        when(authentication.getName()).thenReturn("teacherUser");
        when(teacherService.findByUsername("teacherUser")).thenReturn(teacher);
        when(subjectService.findByTeacherId(1L)).thenReturn(subject);

        String viewName = teacherController.showAddGradeForm(1L, model);

        verify(model, times(1)).addAttribute(eq("subject"), eq(subject));
        verify(model, times(1)).addAttribute(eq("gradeForm"), any(GradeForm.class));
        assertEquals("teacher/add_grade", viewName);
    }

    @Test
    void showAddGradeForm_studentNotFound() {
        when(studentService.findById(1L)).thenReturn(Optional.empty());

        String viewName = teacherController.showAddGradeForm(1L, model);

        verify(model, times(1)).addAttribute("errorMessage", "Student not found");
        assertEquals("error", viewName);
    }

    @Test
    void addGrade() {
        GradeForm gradeForm = new GradeForm();
        gradeForm.setStudentId(1L);
        gradeForm.setGrade(95);
        gradeForm.setDescription("Excellent performance");

        when(authentication.getName()).thenReturn("teacherUser");
        when(teacherService.findByUsername("teacherUser")).thenReturn(teacher);
        when(subjectService.findByTeacherId(1L)).thenReturn(subject);
        when(studentService.findById(1L)).thenReturn(Optional.of(student));

        String viewName = teacherController.addGrade(gradeForm, model);

        verify(gradeService, times(1)).save(any(Grade.class));
        assertEquals("redirect:/teacher/view-class/1", viewName);
    }

    @Test
    void addGrade_subjectNotFound() {
        GradeForm gradeForm = new GradeForm();
        gradeForm.setStudentId(1L);
        gradeForm.setGrade(95);

        when(authentication.getName()).thenReturn("teacherUser");
        when(teacherService.findByUsername("teacherUser")).thenReturn(teacher);
        when(subjectService.findByTeacherId(1L)).thenReturn(null);

        String viewName = teacherController.addGrade(gradeForm, model);

        verify(model, times(1)).addAttribute("errorMessage", "Subject not found for the teacher");
        assertEquals("error", viewName);
    }

    @Test
    void addGrade_studentNotFound() {
        GradeForm gradeForm = new GradeForm();
        gradeForm.setStudentId(1L);
        gradeForm.setGrade(95);

        when(authentication.getName()).thenReturn("teacherUser");
        when(teacherService.findByUsername("teacherUser")).thenReturn(teacher);
        when(subjectService.findByTeacherId(1L)).thenReturn(subject);
        when(studentService.findById(1L)).thenReturn(Optional.empty());

        String viewName = teacherController.addGrade(gradeForm, model);

        verify(model, times(1)).addAttribute("errorMessage", "Student not found");
        assertEquals("error", viewName);
    }

    @Test
    void deleteGrade() {
        when(gradeService.findById(1L)).thenReturn(Optional.of(grade));

        String viewName = teacherController.deleteGrade(1L, model, redirectAttributes);

        verify(gradeService, times(1)).delete(grade);
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Grade deleted successfully");
        assertEquals("redirect:/teacher/view-class/1", viewName);
    }

    @Test
    void deleteGrade_gradeNotFound() {
        when(gradeService.findById(1L)).thenReturn(Optional.empty());

        String viewName = teacherController.deleteGrade(1L, model, redirectAttributes);

        verify(model, times(1)).addAttribute("errorMessage", "Grade not found");
        assertEquals("error", viewName);
    }

    @Test
    void viewProfile() {
        when(authentication.getName()).thenReturn("teacherUser");
        when(userService.findByUsername("teacherUser")).thenReturn(Optional.of(user));

        String viewName = teacherController.viewProfile(model);

        verify(model, times(1)).addAttribute("user", user);
        assertEquals("teacher/view_profile", viewName);
    }

    @Test
    void viewProfile_userNotFound() {
        when(authentication.getName()).thenReturn("teacherUser");
        when(userService.findByUsername("teacherUser")).thenReturn(Optional.empty());

        String viewName = teacherController.viewProfile(model);

        verify(model, times(1)).addAttribute("errorMessage", "User not found.");
        assertEquals("error", viewName);
    }

    @Test
    void logoutConfirmation() {
        String viewName = teacherController.logoutConfirmation();
        assertEquals("teacher/logout_confirmation", viewName);
    }
}
