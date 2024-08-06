package org.example.studentmanagementsystem.config;

import org.example.studentmanagementsystem.repository.ParentRepository;
import org.example.studentmanagementsystem.repository.StudentRepository;
import org.example.studentmanagementsystem.repository.TeacherRepository;
import org.example.studentmanagementsystem.service.UserService;
import org.example.studentmanagementsystem.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/error").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/teacher/**").hasRole("TEACHER")
                                .requestMatchers("/student/**").hasRole("STUDENT")
                                .requestMatchers("/parent/**").hasRole("PARENT")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .successHandler(new CustomSuccessHandler())
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .permitAll()
                )
                .build();
    }

    @Bean
    public UserService userService(UserRepository userRepository,
                                   StudentRepository studentRepository,
                                   ParentRepository parentRepository,
                                   TeacherRepository teacherRepository,
                                   PasswordEncoder passwordEncoder) {
        return new UserService(userRepository,
                studentRepository,
                parentRepository,
                teacherRepository,
                passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}