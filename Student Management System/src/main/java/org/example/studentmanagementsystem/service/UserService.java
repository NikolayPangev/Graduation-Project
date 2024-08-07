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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       ParentRepository parentRepository,
                       TeacherRepository teacherRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found!"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(mapRole(user.getRole())))
                .build();
    }

    private GrantedAuthority mapRole(UserRole role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }

    public boolean adminExists() {
        return userRepository.existsByRole(UserRole.ADMIN);
    }

    public void createAdmin(User admin) {
        if (adminExists()) {
            throw new IllegalStateException("An admin already exists.");
        }
        admin.setRole(UserRole.ADMIN);
        createUser(admin);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createStudent(StudentForm studentForm) {
        validateForm(studentForm.getUsername(), studentForm.getEmail(), studentForm.getPassword(), studentForm.getConfirmPassword());

        Student student = new Student();
        setUserFields(student, studentForm.getUsername(), studentForm.getFirstName(), studentForm.getMiddleName(), studentForm.getLastName(), studentForm.getEmail(), studentForm.getPassword(), UserRole.STUDENT);

        studentRepository.save(student);
    }

    public void createParent(ParentForm parentForm) {
        validateForm(parentForm.getUsername(), parentForm.getEmail(), parentForm.getPassword(), parentForm.getConfirmPassword());

        Parent parent = new Parent();
        setUserFields(parent, parentForm.getUsername(), parentForm.getFirstName(), parentForm.getMiddleName(), parentForm.getLastName(), parentForm.getEmail(), parentForm.getPassword(), UserRole.PARENT);

        parentRepository.save(parent);
    }

    public void createTeacher(TeacherForm teacherForm) {
        validateForm(teacherForm.getUsername(), teacherForm.getEmail(), teacherForm.getPassword(), teacherForm.getConfirmPassword());

        Teacher teacher = new Teacher();
        setUserFields(teacher, teacherForm.getUsername(), teacherForm.getFirstName(), teacherForm.getMiddleName(), teacherForm.getLastName(), teacherForm.getEmail(), teacherForm.getPassword(), UserRole.TEACHER);

        teacherRepository.save(teacher);
    }

    private void validateForm(String username, String email, String password, String confirmPassword) {
        if (usernameExists(username)) {
            throw new IllegalStateException("Username already exists.");
        }
        if (emailExists(email)) {
            throw new IllegalStateException("Email already exists.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalStateException("Passwords do not match.");
        }
    }

    private void setUserFields(User user, String username, String firstName, String middleName, String lastName, String email, String password, UserRole role) {
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
    }

    private void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
