package org.example.studentmanagementsystem.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherForm {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;

    @Size(min = 6, message = "Password should be at least 6 characters")
    private String confirmPassword;
}
