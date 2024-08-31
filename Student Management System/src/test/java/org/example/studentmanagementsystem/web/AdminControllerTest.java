package org.example.studentmanagementsystem.web;

import org.example.studentmanagementsystem.model.dtos.StudentForm;
import org.example.studentmanagementsystem.model.entities.Class;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.repository.ClassRepository;
import org.example.studentmanagementsystem.service.StudentService;
import org.example.studentmanagementsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private StudentService studentService;


    @Mock
    private ClassRepository classRepository;


    @InjectMocks
    private AdminController adminController;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDashboard() {
        String view = adminController.getDashboard();
        assertEquals("admin/admin_dashboard", view);
    }

    @Test
    public void testViewProfile() {
        when(principal.getName()).thenReturn("testUser");
        User user = new User();
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        String view = adminController.viewProfile(model, principal);

        assertEquals("admin/view_profile", view);
        verify(model, times(1)).addAttribute("user", user);
    }

    @Test
    public void testCreateStudentForm() {
        String view = adminController.createStudentForm(model);
        assertEquals("admin/register_student", view);
        verify(model, times(1)).addAttribute("studentForm", new StudentForm());
    }

    @Test
    public void testCreateStudent_ValidForm() {
        StudentForm studentForm = new StudentForm();
        studentForm.setUsername("student");
        studentForm.setEmail("student@example.com");
        studentForm.setPassword("password");
        studentForm.setConfirmPassword("password");

        when(userService.usernameExists(anyString())).thenReturn(false);
        when(userService.emailExists(anyString())).thenReturn(false);

        String view = adminController.createStudent(studentForm, bindingResult, redirectAttributes);

        assertEquals("redirect:/admin/createStudent", view);
        verify(userService, times(1)).createStudent(studentForm);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Student " + studentForm.getFirstName() + " " + studentForm.getLastName() + " was successfully registered!");
    }

    @Test
    public void testCreateStudent_InvalidForm() {
        StudentForm studentForm = new StudentForm();
        studentForm.setUsername("student");
        studentForm.setEmail("student@example.com");
        studentForm.setPassword("password");
        studentForm.setConfirmPassword("differentPassword");

        when(userService.usernameExists(anyString())).thenReturn(false);
        when(userService.emailExists(anyString())).thenReturn(false);

        String view = adminController.createStudent(studentForm, bindingResult, redirectAttributes);

        assertEquals("createStudent", view);
        verify(userService, never()).createStudent(any(StudentForm.class));
    }

//    @Test
//    public void testAssignClassToStudent() {
//        Student student = new Student();
//        Class clazz = new Class();
//        when(studentService.findById(anyLong())).thenReturn(Optional.of(student));
//        when(classRepository.findById(anyLong())).thenReturn(Optional.of(clazz));
//
//        String view = adminController.assignClassToStudent(1L, 1L);
//
//        assertEquals("redirect:/admin/viewStudents", view);
//        assertEquals(clazz, student.getClasses());
//        verify(studentService, times(1)).save(student);
//    }

    @Test
    public void testDeleteStudent() {
        doNothing().when(studentService).deleteStudent(anyLong());

        String view = adminController.deleteStudent(1L, redirectAttributes);

        assertEquals("redirect:/admin/viewStudents", view);
        verify(studentService, times(1)).deleteStudent(1L);
        verify(redirectAttributes, times(1)).addFlashAttribute("successMessage", "Student successfully deleted");
    }
}
