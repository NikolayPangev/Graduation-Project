package org.example.studentmanagementsystem.model.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    @NotEmpty(message = "Username is required")
    @Size(min = 6)
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;

}
