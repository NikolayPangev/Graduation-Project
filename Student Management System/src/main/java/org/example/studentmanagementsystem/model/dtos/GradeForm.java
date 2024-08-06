package org.example.studentmanagementsystem.model.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeForm {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Grade is required")
    private Integer grade;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
