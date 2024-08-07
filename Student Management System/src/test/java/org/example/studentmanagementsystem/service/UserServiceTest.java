package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.dtos.ParentForm;
import org.example.studentmanagementsystem.model.dtos.StudentForm;
import org.example.studentmanagementsystem.model.dtos.TeacherForm;
import org.example.studentmanagementsystem.model.entities.Parent;
import org.example.studentmanagementsystem.model.entities.Student;
import org.example.studentmanagementsystem.model.entities.Teacher;
import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.model.enums.UserRole;
import org.example.studentmanagementsystem.repository.ParentRepository;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.TeacherRepository;
import org.example.studentmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private StudentForm studentForm;
    private ParentForm parentForm;
    private TeacherForm teacherForm;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);

        studentForm = new StudentForm();
        studentForm.setUsername("teststudent");
        studentForm.setFirstName("Test");
        studentForm.setMiddleName("Middle");
        studentForm.setLastName("Student");
        studentForm.setEmail("teststudent@example.com");
        studentForm.setPassword("password");
        studentForm.setConfirmPassword("password");

        parentForm = new ParentForm();
        parentForm.setUsername("testparent");
        parentForm.setFirstName("Test");
        parentForm.setMiddleName("Middle");
        parentForm.setLastName("Parent");
        parentForm.setEmail("testparent@example.com");
        parentForm.setPassword("password");
        parentForm.setConfirmPassword("password");

        teacherForm = new TeacherForm();
        teacherForm.setUsername("testteacher");
        teacherForm.setFirstName("Test");
        teacherForm.setMiddleName("Middle");
        teacherForm.setLastName("Teacher");
        teacherForm.setEmail("testteacher@example.com");
        teacherForm.setPassword("password");
        teacherForm.setConfirmPassword("password");
    }

    @Test
    void loadUserByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertNotNull(userService.loadUserByUsername("testuser"));
    }

    @Test
    void loadUserByUsername_UserDoesNotExist() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));
    }

    @Test
    void adminExists_AdminExists() {
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(true);
        assertTrue(userService.adminExists());
    }

    @Test
    void adminExists_AdminDoesNotExist() {
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);
        assertFalse(userService.adminExists());
    }

    @Test
    void createAdmin_AdminAlreadyExists() {
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> userService.createAdmin(user));
    }

    @Test
    void createAdmin_AdminDoesNotExist() {
        when(userRepository.existsByRole(UserRole.ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        userService.createAdmin(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertTrue(userService.findByUsername("testuser").isPresent());
    }

    @Test
    void findByUsername_UserDoesNotExist() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        assertFalse(userService.findByUsername("nonexistentuser").isPresent());
    }

    @Test
    void usernameExists_UsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertTrue(userService.usernameExists("testuser"));
    }

    @Test
    void usernameExists_UsernameDoesNotExist() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        assertFalse(userService.usernameExists("nonexistentuser"));
    }

    @Test
    void emailExists_EmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        assertTrue(userService.emailExists("test@example.com"));
    }

    @Test
    void emailExists_EmailDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        assertFalse(userService.emailExists("nonexistent@example.com"));
    }

    @Test
    void createStudent() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        userService.createStudent(studentForm);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createParent() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        userService.createParent(parentForm);
        verify(parentRepository, times(1)).save(any(Parent.class));
    }

    @Test
    void createTeacher() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        userService.createTeacher(teacherForm);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void validateForm_UsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        assertThrows(IllegalStateException.class, () -> userService.createStudent(studentForm));
    }

    @Test
    void validateForm_EmailExists() {
        when(userRepository.findByEmail("teststudent@example.com")).thenReturn(Optional.of(user));
        assertThrows(IllegalStateException.class, () -> userService.createStudent(studentForm));
    }

    @Test
    void validateForm_PasswordsDoNotMatch() {
        studentForm.setConfirmPassword("differentpassword");
        assertThrows(IllegalStateException.class, () -> userService.createStudent(studentForm));
    }
}