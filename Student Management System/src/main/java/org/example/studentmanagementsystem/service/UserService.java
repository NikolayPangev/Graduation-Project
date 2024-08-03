package org.example.studentmanagementsystem.service;

import org.example.studentmanagementsystem.model.dtos.StudentForm;
import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.model.enums.UserRole;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
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

    public void createStudent(StudentForm studentForm, String encodedPassword) {
        User user = new User();
        user.setUsername(studentForm.getUsername());
        user.setFirstName(studentForm.getFirstName());
        user.setMiddleName(studentForm.getMiddleName());
        user.setLastName(studentForm.getLastName());
        user.setEmail(studentForm.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.STUDENT);

        userRepository.save(user);
    }
}
