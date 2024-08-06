package org.example.studentmanagementsystem.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.studentmanagementsystem.model.entities.Grade;
import org.example.studentmanagementsystem.model.entities.Subject;
import org.example.studentmanagementsystem.model.entities.Teacher;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubjectWithGrades {
    private Subject subject;
    private List<Grade> grades;
    private double averageGrade;
    private Teacher teacher;
}