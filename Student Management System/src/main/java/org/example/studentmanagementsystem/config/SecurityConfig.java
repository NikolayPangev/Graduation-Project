package org.example.studentmanagementsystem.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Allow access to static resources for everyone
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                // Public pages accessible to everyone
                                .requestMatchers("/", "/login", "/error").permitAll()
                                // Role-based access control
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/teacher/**").hasRole("TEACHER")
                                .requestMatchers("/student/**").hasRole("STUDENT")
                                .requestMatchers("/parent/**").hasRole("PARENT")
                                // Any other request needs authentication
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/login?error=true")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout=true")
                                .invalidateHttpSession(true)
                                .permitAll()
                )
                .build();
    }

//    @Bean
//    public UserDetailsService customUserDetailsService() {
//        return new UserDetailsService();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder
                .defaultsForSpringSecurity_v5_8();
    }
}
