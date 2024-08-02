package org.example.studentmanagementsystem.config;

import org.example.studentmanagementsystem.model.entities.User;
import org.example.studentmanagementsystem.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
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
                admin.setPassword("Admin123");
                userService.createAdmin(admin);
            }
        };
    }
}


