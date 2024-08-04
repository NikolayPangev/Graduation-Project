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
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        userRepository.save(admin);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void createStudent(StudentForm studentForm) {
        if (usernameExists(studentForm.getUsername())) {
            throw new IllegalStateException("Username already exists.");
        }
        if (emailExists(studentForm.getEmail())) {
            throw new IllegalStateException("Email already exists.");
        }
        if (!studentForm.getPassword().equals(studentForm.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match.");
        }

        Student student = new Student();
        student.setUsername(studentForm.getUsername());
        student.setFirstName(studentForm.getFirstName());
        student.setMiddleName(studentForm.getMiddleName());
        student.setLastName(studentForm.getLastName());
        student.setEmail(studentForm.getEmail());
        student.setPassword(passwordEncoder.encode(studentForm.getPassword()));
        student.setRole(UserRole.STUDENT);

        studentRepository.save(student);
    }

    public void createParent(ParentForm parentForm) {
        if (usernameExists(parentForm.getUsername())) {
            throw new IllegalStateException("Username already exists.");
        }
        if (emailExists(parentForm.getEmail())) {
            throw new IllegalStateException("Email already exists.");
        }
        if (!parentForm.getPassword().equals(parentForm.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match.");
        }

        Parent parent = new Parent();
        parent.setUsername(parentForm.getUsername());
        parent.setFirstName(parentForm.getFirstName());
        parent.setMiddleName(parentForm.getMiddleName());
        parent.setLastName(parentForm.getLastName());
        parent.setEmail(parentForm.getEmail());
        parent.setPassword(passwordEncoder.encode(parentForm.getPassword()));
        parent.setRole(UserRole.PARENT);

        parentRepository.save(parent);
    }

    public void createTeacher(TeacherForm teacherForm) {
        if (usernameExists(teacherForm.getUsername())) {
            throw new IllegalStateException("Username already exists.");
        }
        if (emailExists(teacherForm.getEmail())) {
            throw new IllegalStateException("Email already exists.");
        }
        if (!teacherForm.getPassword().equals(teacherForm.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match.");
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(teacherForm.getUsername());
        teacher.setFirstName(teacherForm.getFirstName());
        teacher.setMiddleName(teacherForm.getMiddleName());
        teacher.setLastName(teacherForm.getLastName());
        teacher.setEmail(teacherForm.getEmail());
        teacher.setPassword(passwordEncoder.encode(teacherForm.getPassword()));
        teacher.setRole(UserRole.TEACHER);

        teacherRepository.save(teacher);
    }

}
