package org.example.studentmanagementsystem.config;

import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            if (!userService.adminExists()) {
                User admin = new User();
                admin.setUsername("Admin");
                admin.setFirstName("Admin");
                admin.setLastName("Admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("Admin123"));
                userService.createAdmin(admin);
            }
        };
    }
}


