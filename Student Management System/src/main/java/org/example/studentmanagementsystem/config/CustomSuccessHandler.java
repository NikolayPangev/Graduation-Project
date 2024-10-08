package org.example.studentmanagementsystem.config;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/";

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            redirectUrl = "/admin/dashboard"; // Admin dashboard
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            redirectUrl = "/teacher/dashboard"; // Teacher dashboard
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            redirectUrl = "/student/dashboard"; // Student dashboard
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_PARENT"))) {
            redirectUrl = "/parent/dashboard"; // Parent dashboard
        }

        response.sendRedirect(redirectUrl);
    }
}


