package org.example.studentmanagementsystem.model.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private Double averageGrade;
    private List<String> teacherNames;
}
